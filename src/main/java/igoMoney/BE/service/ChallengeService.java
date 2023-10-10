package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.Challenge;
import igoMoney.BE.domain.ChallengeUser;
import igoMoney.BE.domain.Notification;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.request.ChallengeCreateRequest;
import igoMoney.BE.dto.response.ChallengeResponse;
import igoMoney.BE.dto.response.ChallengeTotalCostResponse;
import igoMoney.BE.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final NotificationRepository notificationRepository;
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM월 dd일");

    // 시작 안 한 챌린지 목록 조회
    public List<ChallengeResponse> getNotStartedChallengeList() {

        List<ChallengeResponse> responseList = new ArrayList<>();
        List<Challenge> challengeList = challengeRepository.findAllByStartDateIsNull();
        for (Challenge challenge: challengeList){
            ChallengeResponse challengeResponse = ChallengeResponse.builder()
                    .id(challenge.getId())
                    .leaderId(challenge.getLeaderId())
                    .title(challenge.getTitle())
                    .content(challenge.getContent())
                    .targetAmount(challenge.getTargetAmount())
                    .build();
            responseList.add(challengeResponse);
        }

        return responseList;
    }

    // 참여중인 챌린지 기본정보 조회
    public ChallengeResponse getMyActiveChallenge(Long userId) {

        User findUser = getUserOrThrow(userId);
        if (!findUser.getInChallenge()){
            throw new CustomException(ErrorCode.USER_NOT_IN_CHALLENGE);
        }
        Challenge challenge = challengeRepository.findById(findUser.getMyChallengeId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
        User user2 = getChallengeOtherUser(challenge.getId(), userId);
        Long user2Id;
        if(user2 == null) {
            user2Id = null;
        } else{
            user2Id = user2.getId();
        }

        ChallengeResponse response = ChallengeResponse.builder()
                .id(challenge.getId())
                .leaderId(challenge.getLeaderId())
                .competitorId(user2Id)
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .categoryId(challenge.getCategoryId())
                .targetAmount(challenge.getTargetAmount())
                .build();

        return response;
    }

    // 챌린지 등록하기
    public Long createChallenge(ChallengeCreateRequest request) {

        User findUser = getUserOrThrow(request.getUserId());
        // 이미 참여중인 챌린지가 있거나 (시작 대기중인)등록한 챌린지가 있음. 챌린지 종료 후 다시 등록 가능
        if (findUser.getInChallenge() != false) {
            throw new CustomException(ErrorCode.EXIST_USER_CHALLENGE);
        }

        Challenge challenge = Challenge.builder()
                .leaderId(request.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .categoryId(request.getCategoryId())
                .startDate(request.getStartDate())
                .targetAmount(request.getTargetAmount())
                .build();

        challengeRepository.save(challenge);
        // 회원 - 챌린지 참여중으로 상태 변경
        // 회원 - 현재 진행중인 챌린지 저장
        findUser.updateUser(true, challenge.getId());
        findUser.addChallengeCount();

        ChallengeUser challengeUser = ChallengeUser.builder()
                .user(findUser)
                .challenge(challenge)
                .build();
        challengeUserRepository.save(challengeUser);
        challenge.addChallengeUser(challengeUser);
        return challenge.getId();
    }

    // 챌린지 참여 신청하기
    public void applyChallenge(Long userId, Long challengeId) {

        // 신청 가능여부 확인
        User findUser = getUserOrThrow(userId);
        if (findUser.getInChallenge()){
            throw new CustomException(ErrorCode.EXIST_USER_CHALLENGE);
        }
        Challenge findChallenge = getChallengeOrThrow(challengeId);
        findUser.updateUser(true, challengeId); // 챌린지 참여 정보 업데이트
        findUser.addChallengeCount();

        ChallengeUser challengeUser  = ChallengeUser.builder()
                        .user(findUser)
                        .challenge(findChallenge)
                        .build();
        challengeUserRepository.save(challengeUser);
        findChallenge.addChallengeUser(challengeUser); // 연관관계 설정

        // 상대방에게 챌린지 참가 신청 알림 보내기
        User user2 = getChallengeOtherUser(challengeId, userId);
        Notification notification = Notification.builder()
                .user(user2)
                .title("챌린지 현황")
                .message(findChallenge.getStartDate().format(dateFormat)+"부터 "+findUser.getNickname()+"님과 챌린지 시작")
                .build();
        notificationRepository.save(notification);

    }

    // 챌린지 포기하기
    public void giveupChallenge(Long userId) {

        // 포기 가능한 상태인지 확인
        User findUser = getUserOrThrow(userId);
        if (!findUser.getInChallenge()){
            throw new CustomException(ErrorCode.USER_NOT_IN_CHALLENGE);
        }
        cancelChallenge(findUser);
    }

    // 회원탈퇴시 챌린지 포기 - 에러코드 차이 때문에 별도 메서드로 정의
    public void giveUpChallengeSignOut(Long userId){

        User findUser = getUserOrThrow(userId);
        if (!findUser.getInChallenge()){
            return;
        }
        cancelChallenge(findUser);
    }

    private void cancelChallenge(User user) {

        Challenge findChallenge = getChallengeOrThrow(user.getMyChallengeId());

        findChallenge.stopChallenge(); // 챌린지 중단 설정
        user.updateUser(false, null);  // 사용자 챌린지 상태 변경

        User user2 = getChallengeOtherUser(user.getMyChallengeId(), user.getId());
        if (user2 == null){ return;} // 상대방 없을 때

        // 상대방 있을 때
        user.deleteBadge(); // 뱃지 개수 차감하기
        user2.updateUser(false, null); // 상대방 챌린지 상태 변경
        user2.addBadge();
        user2.addWinCount();
        findChallenge.setWinner(user2.getId());


        // 상대방에게 챌린지 중단 알림 보내기
        Notification notification = Notification.builder()
                .user(user2)
                .title(user.getNickname() + "님과의 챌린지 중단")
                .message("상대방 "+ user.getNickname() +"님이 챌린지를 포기했어요.")
                .build();
        notificationRepository.save(notification);
    }

    // 챌린지의 각 사용자별 누적금액 조회
    public List<ChallengeTotalCostResponse> getTotalCostPerChallengeUser(Long challengeId) {

        List<ChallengeTotalCostResponse> responseList = new ArrayList<>();
        List<Object[]> totalCosts =  recordRepository.calculateTotalCostByUserId(challengeId);
        for (Object[] obj: totalCosts){

            ChallengeTotalCostResponse challengeTotalCostResponse = ChallengeTotalCostResponse.builder()
                    .userId((Long) obj[0])
                    .totalCost(((BigDecimal) obj[1]).intValue()) // BigInteger
                    .build();
            responseList.add(challengeTotalCostResponse);
        }

        return responseList;
    }

    // 챌린지 완료 (마지막날까지 성공)
    @Scheduled(cron="0 0 0 * * *", zone = "Asia/Seoul") // 초 분 시 일 월 요일
    public void finishChallenge() {

        List<Challenge> challengeList = challengeRepository.findAllByStatus("inProgress");
        Integer minCost = 99999999;
        Long winnerId = null;
        Boolean check =false;
        Integer tempCost = 99999999;
        for (Challenge c : challengeList) {
            if (c.getStartDate().plusDays(7).equals(LocalDate.now())){
                // Challenge : 챌린지 종료 설정
                c.finishChallenge();

                // 챌린지 승자 결정
                List<Object[]> totalCosts =  recordRepository.calculateTotalCostByUserId(c.getId());
                for (Object[] obj: totalCosts){
                    if(((BigDecimal) obj[1]).intValue() == minCost){
                        check = true;
                        tempCost = minCost;
                    }
                    else if (((BigDecimal) obj[1]).intValue() < minCost){
                        minCost = ((BigDecimal) obj[1]).intValue();
                        winnerId = (Long) obj[0];
                    }
                }
                // 동점자 처리
                List<User> userList = getAllChallengeUser(c.getId());
                User findWinner = getUserOrThrow(winnerId);
                if (tempCost == minCost){
                    c.setWinner(-1L);
                    for (User u : userList) {
                        u.addBadge();
                        u.addWinCount();
                    }
                } else {
                    c.setWinner(winnerId);
                    findWinner.addBadge();
                    findWinner.addWinCount();
                }

                // 챌린지 완료 알림
                User lose = getChallengeOtherUser(c.getId(), winnerId);
                for(User u : userList) {
                    // 유저 : 챌린지 종료로 설정
                    u.updateUser(false, null);

                    if (u.getId().equals(winnerId)) {
                        // 챌린지 승리자
                        Notification notification = Notification.builder()
                                .user(u)
                                .title(lose.getNickname()+"님과의 챌린지 완료")
                                .message(u.getNickname()+"님! 챌린지에서 승리하셨어요 \uD83E\uDD47") // 🥇
                                .build();
                        notificationRepository.save(notification);
                    } else{
                        Notification notification = Notification.builder()
                                .user(u)
                                .title(findWinner.getNickname()+"님과의 챌린지 완료")
                                .message(u.getNickname()+"님이 챌린지에서 승리하셨어요 \uD83D\uDE25") //😥
                                .build();
                        notificationRepository.save(notification);
                    }

                }
            }
        }


    }


    // 예외 처리 - 존재하는 challenge 인가
    private Challenge getChallengeOrThrow(Long id) {

        return challengeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
    }

    // 예외 처리 - 존재하는 user 인가
    private User getUserOrThrow(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    // 챌린지 상대방 정보 조회
    private User getChallengeOtherUser(Long challengeId, Long userId) {
        List<ChallengeUser> ChallengeUserList = challengeUserRepository.findAllByChallengeId(challengeId);
        for (ChallengeUser c : ChallengeUserList) {
            if(c.getUser().getId() != userId){
                return c.getUser();
            }
        }
        return null;
    }

    // 챌린지 참가자 정보 조회
    private List<User> getAllChallengeUser(Long challengeId) {

        List<User> userList = new ArrayList<>();
        List<ChallengeUser> challengeUserList = challengeUserRepository.findAllByChallengeId(challengeId);
        for (ChallengeUser c : challengeUserList) {
            userList.add(c.getUser());
        }
        return userList;
    }
}
