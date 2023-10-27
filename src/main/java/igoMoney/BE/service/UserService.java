package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.Notification;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.response.NotificationResponse;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.repository.NotificationRepository;
import igoMoney.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ImageService imageService;

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {

        User findUser = getUserOrThrow(userId);
        String imageUrl = imageService.processImage(findUser.getImage());

        UserResponse response = UserResponse.builder()
                .id(findUser.getId())
                .provider(findUser.getProvider())
                .email(findUser.getEmail())
                .nickname(findUser.getNickname())
                .image(imageUrl)
                .role(findUser.getRole())
                .inChallenge(findUser.getInChallenge())
                .challengeCount(findUser.getChallengeCount())
                .winCount(findUser.getWinCount())
                .badgeCount(findUser.getBadgeCount())
                .build();

        return response;
    }


    // 닉네임 중복확인
    @Transactional(readOnly = true)
    public void checkNicknameDuplicate(String nickname) {

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.EXIST_USER_NICKNAME);
        }
    }


    // 회원 닉네임 변경하기 (회원가입 시 빈칸으로 가입됨. 닉네임 설정 후 홈화면 나옴)
    public void changeUserNickname(Long userId, String nickname) {

        User findUser = getUserOrThrow(userId);
        checkNicknameDuplicate(nickname);
        findUser.updateNickname(nickname);
    }

    // 회원 프로필 이미지 변경
    public void changeProfileImage(Long userId, MultipartFile image) throws IOException {

        User findUser = getUserOrThrow(userId);
        String uuid = null;
        if(image.isEmpty()){
            throw new CustomException(ErrorCode.SHOULD_EXIST_IMAGE);
        }
        uuid = imageService.uploadImage(image);
        findUser.updateProfileImage(uuid);
    }

    // 확인 안한 알림 목록 조회
    public List<NotificationResponse> getUncheckedNotificationList(Long userId) {

        List<Notification> findNotificationList  = notificationRepository.findAllByUserIdAndCheckedIsFalse(userId);
        List<NotificationResponse> responseList = findNotificationList.stream()
                .map(m -> NotificationResponse.builder()
                        .notificationId(m.getId())
                        .userId(m.getUser().getId())
                        .title(m.getTitle())
                        .message(m.getMessage())
                        .build())
                .collect(Collectors.toList());

        return responseList;
    }

    // 알림 확인 체크
    public void checkNotificationToRead(Long notificationId){

        Notification findNotification = getNotificationOrThrow(notificationId);
        findNotification.markChecked();
    }


    // 예외 처리 - 존재하는 user인지
    public User getUserOrThrow(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    // 예외 처리 - 존재하는 notification인지
    public Notification getNotificationOrThrow(Long id) {

        return notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));
    }
}
