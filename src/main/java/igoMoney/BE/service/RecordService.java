package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.Challenge;
import igoMoney.BE.domain.Record;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.request.RecordSaveRequest;
import igoMoney.BE.dto.response.RecordResponse;
import igoMoney.BE.repository.ChallengeRepository;
import igoMoney.BE.repository.RecordRepository;
import igoMoney.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final ChallengeRepository challengeRepository;

    // record 등록하기
    public Long saveRecord (RecordSaveRequest request) {

        User findUser = getUserOrThrow(request.getUserId());
        Challenge challenge = getChallengeOrThrow(request.getUserId());
        checkPermission(findUser, request.getChallengeId());
        Record record = Record.builder()
                .challenge(challenge)
                .user(findUser)
                .title(request.getTitle())
                .content(request.getContent())
                .image(request.getImage())
                .cost(request.getCost())
                .date(LocalDate.now())
                .build();
        recordRepository.save(record);

        return record.getId();
    }


    // record 1건 조회
    public RecordResponse getRecord(Long recordId) {

        Record record = getRecordOrThrow(recordId);

//        String imageUrl = imageService.processImage(record.getImage());
        RecordResponse response = RecordResponse.builder()
                .recordId(record.getId())
                .challengeId(record.getChallenge().getId())
                .userId(record.getUser().getId())
                .title(record.getTitle())
                .content(record.getContent())
                .cost(record.getCost())
                //.image(imageUrl)
                .date(record.getDate())
                .build();

        return response;
    }


    // 사용자의 그날의 모든 record 조회
    public List<RecordResponse> getUserDailyRecordList(Long userId, LocalDate date) {

        List<RecordResponse> responseList = new ArrayList<>();
        List<Record> recordList = recordRepository.findAllByUserIdAndDate(userId, date);
        for (Record record: recordList){
//            String image = imageService.processImage(record.getBeforeImage());
            RecordResponse recordResponse = RecordResponse.builder()
                    .recordId(record.getId())
                    .challengeId(record.getChallenge().getId())
                    .userId(record.getUser().getId())
                    .title(record.getTitle())
                    .content(record.getContent())
                    .cost(record.getCost())
                    //.image(imageUrl)
                    .date(record.getDate())
                    .build();

            responseList.add(recordResponse);
        }

        return responseList;
    }

    // 예외 처리 - 존재하는 record 인가
    private Record getRecordOrThrow(Long id) {

        return recordRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_RECORD));
    }

    // 예외 처리 - 존재하는 User 인가
    private User getUserOrThrow(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    // 예외 처리 - 존재하는 challenge 인가
    private Challenge getChallengeOrThrow(Long id) {

        return challengeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CHALLENGE));
    }

    // 예외 처리 - 채린지 인증글 작성 권한을 가진 user 인가
    private void checkPermission(User user, Long ChallengeId) {

        if (!user.getMyChallengeId().equals(ChallengeId)) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

}
