package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.Challenge;
import igoMoney.BE.domain.ChallengeUser;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.request.ChallengeCreateRequest;
import igoMoney.BE.dto.response.ChallengeResponse;
import igoMoney.BE.repository.ChallengeRepository;
import igoMoney.BE.repository.ChallengeUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeUserRepository challengeUserRepository;
    private final UserService userService;

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

        User findUser = userService.getUserOrThrow(userId);
        if (!findUser.getInChallenge()){
            throw new CustomException(ErrorCode.USER_NOT_IN_CHALLENGE);
        }
        Challenge challenge = challengeRepository.findById(findUser.getMyChallengeId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));

        ChallengeResponse response = ChallengeResponse.builder()
                .id(challenge.getId())
                .leaderId(challenge.getLeaderId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .targetAmount(challenge.getTargetAmount())
                .build();

        return response;
    }

    // 챌린지 등록하기
    public Long createChallenge(ChallengeCreateRequest request) {

        User findUser = userService.getUserOrThrow(request.getUserId());
        // 이미 참여중인 챌린지가 있거나 (시작 대기중인)등록한 챌린지가 있음. 챌린지 종료 후 다시 등록 가능
        if (findUser.getInChallenge() != false) {
            throw new CustomException(ErrorCode.EXIST_USER_CHALLENGE);
        }

        Challenge challenge = Challenge.builder()
                .leaderId(request.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .targetAmount(request.getTargetAmount())
                .build();

        challengeRepository.save(challenge);
        // 회원 - 챌린지 참여중으로 상태 변경
        // 회원 - 현재 진행중인 챌린지 저장
        findUser.updateUser(true, challenge.getId());

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
        User findUser = userService.getUserOrThrow(userId);
        if (findUser.getInChallenge()){
            throw new CustomException(ErrorCode.EXIST_USER_CHALLENGE);
        }
        Challenge findChallenge = getChallengeOrThrow(challengeId);
        findChallenge.startChallenge(); // 챌린지 다음날부터 시작하는 것으로 설정
        findUser.updateUser(true, challengeId); // 챌린지 참여 정보 업데이트

        ChallengeUser challengeUser  = ChallengeUser.builder()
                        .user(findUser)
                        .challenge(findChallenge)
                        .build();
        challengeUserRepository.save(challengeUser);
        findChallenge.addChallengeUser(challengeUser); // 연관관계 설정

        // 상대방에게 챌린지 참가 신청 알림 보내기
    }

    // 챌린지 포기하기
    public void giveupChallenge(Long userId, Long challengeId) {

        // 포기 가능한 상태인지 확인
        User findUser = userService.getUserOrThrow(userId);
        if (!findUser.getInChallenge()){
            throw new CustomException(ErrorCode.USER_NOT_IN_CHALLENGE);
        }
        Challenge findChallenge = getChallengeOrThrow(challengeId);

        findChallenge.stopChallenge(); // 챌린지 중단 설정
        findUser.updateUser(false, null);  // 사용자 챌린지 상태 변경
        findUser.deleteBadge(); // 뱃지 개수 차감하기

        User user2 = getChallengeOtherUser(challengeId, userId); // 상대방 챌린지 상태 변경
        if (user2 == null){ return;} // 상대방 없을 때
        user2.updateUser(false, null);


        // 상대방에게 챌린지 중단 알림 보내기
    }

    // 예외 처리 - 존재하는 challenge 인가
    private Challenge getChallengeOrThrow(Long id) {

        return challengeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
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

}
