package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.*;
import igoMoney.BE.domain.Record;
import igoMoney.BE.dto.request.RecordReportRequest;
import igoMoney.BE.dto.request.RecordSaveRequest;
import igoMoney.BE.dto.request.RecordUpdateRequest;
import igoMoney.BE.dto.response.RecordResponse;
import igoMoney.BE.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private final UserReportRepository userReportRepository;
    private final NotificationService notificationService;
    private final ImageService imageService;
    private final ChallengeService challengeService;

    private List<String> reportReasons = List.of("스팸", "나체 이미지/음란한 내용", "상품 판매 및 홍보", "자해/자살", "저작권/명에훼손/기타 권리 침해", "특정인의 개인정보 포함", "혐오 조장 내용");
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    // record 등록하기
    public Long saveRecord (RecordSaveRequest request) throws IOException {

        User findUser = getUserOrThrow(request.getUserId());
        Challenge challenge = getChallengeOrThrow(request.getChallengeId());
        checkPermission(findUser, request.getChallengeId());

        List<Image> image = multipartFilesToUUIDStrings(request.getImage());
        Record record = Record.builder()
                .challenge(challenge)
                .user(findUser)
                .title(request.getTitle())
                .content(request.getContent())
                .image(image)
                .cost(request.getCost())
                .date(LocalDate.now())
                .build();
        recordRepository.save(record);

        return record.getId();
    }


    // record 1건 조회
    public RecordResponse getRecord(Long recordId) {

        Record record = getRecordOrThrow(recordId);
        return recordToRecordResponse(record);
    }

    // 사용자의 그날의 모든 record 조회
    public List<RecordResponse> getUserDailyRecordList(Long userId, LocalDate date) {

        List<RecordResponse> responses = new ArrayList<>();
        List<Record> records = recordRepository.findAllByUserIdAndDate(userId, date);
        for (Record record: records){
            responses.add(recordToRecordResponse(record));
        }
        return responses;
    }

    // 사용자의 모든 record 삭제 (회원탈퇴 시)
    public void deleteAllUserRecords(Long userId) {

        List<Record> recordList = recordRepository.findAllByUserId(userId);
        for(Record r : recordList){
            recordRepository.delete(r);
        }
    }

    // record 삭제
    public void deleteRecord(Long recordId) {

        Record findRecord = getRecordOrThrow(recordId);
        recordRepository.delete(findRecord);
    }

    // record 수정
    public void updateRecord(RecordUpdateRequest request) throws IOException {

        Record findRecord = getRecordOrThrow(request.getRecordId());
        List<Image> imageUUIDs = multipartFilesToUUIDStrings(request.getImage());
        findRecord.updateRecord(request.getTitle(), request.getContent(), request.getCost(), imageUUIDs);
    }

    public Long reportRecord(RecordReportRequest request) {

        UserReport userReport = UserReport.builder()
                .reporterId(request.getReporter_userId())
                .offenderId(request.getOffender_userId())
                .recordId(request.getRecordId())
                .reason(request.getReason())
                .build();

        userReportRepository.save(userReport);
        User offender = getUserOrThrow(request.getOffender_userId());
        offender.addReportedCount();
        // 신고 알림
        Notification reportNotification = Notification.builder()
                .user(offender)
                .title("챌린지 현황")
                .message(offender.getNickname()+"님 지출 내역은 가이드라인 위반으로 인해 삭제 되었어요. 신고 내용 확인 후 조치가 취해질 예정입니다. 신고 사유: "+reportReasons.get(request.getReason()))
                .build();
        notificationService.makeNotification(reportNotification);

        // hide record
        Record record = getRecordOrThrow(request.getRecordId());
        record.setHidden();

        if (offender.getReportedCount() >= 3 ){
            offender.setBanned();
            offender.setBanReleaseDate(LocalDate.now().plusDays(7));
            // 신고 알림
            Notification banNotification = Notification.builder()
                    .user(offender)
                    .title("챌린지 결과")
                    .message(offender.getNickname()+"님은 신고 누적으로 진행중인 챌린지는 패배처리되고 일주일 동안 챌린지 참여가 제한됩니다. 제한 해제 날짜: " + offender.getBanReleaseDate().format(dateFormat))
                    .build();
            notificationService.makeNotification(banNotification);
            challengeService.cancelChallenge(offender, 2);
        }
        return userReport.getId();
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

    private List<Image> multipartFilesToUUIDStrings(List<MultipartFile> multipartFiles) throws IOException {

        if ((multipartFiles == null) || multipartFiles.isEmpty()) {
            throw new CustomException(ErrorCode.SHOULD_EXIST_IMAGE);
        }
        List<Image> uuids = new ArrayList<>();
        for (MultipartFile m : multipartFiles){
            checkExistsImage(m);
            uuids.add(Image.builder().uuid(imageService.uploadImage(m)).build());
        }
        return uuids;
    }

    private RecordResponse recordToRecordResponse(Record record) {
        List<String> imageUrls = new ArrayList<>();
        for (Image i : record.getImage()){
            imageUrls.add(imageService.processImage(i.getUuid()));
        }
        return RecordResponse.builder()
                .recordId(record.getId())
                .challengeId(record.getChallenge().getId())
                .userId(record.getUser().getId())
                .title(record.getTitle())
                .content(record.getContent())
                .cost(record.getCost())
                .image(imageUrls)
                .date(record.getDate())
                .hide(record.getHide())
                .build();
    }

    // 예외 처리 - 이미지 존재여부
    private void checkExistsImage(MultipartFile image) {

        if ((image == null) || image.isEmpty()) {
            throw new CustomException(ErrorCode.SHOULD_EXIST_IMAGE);
        }
    }
}
