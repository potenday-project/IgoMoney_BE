package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {

        User findUser = getUserOrThrow(userId);

        UserResponse response = UserResponse.builder()
                .id(findUser.getId())
                .email(findUser.getEmail())
                .nickname(findUser.getNickname())
                .image(findUser.getImage())
                .role(findUser.getRole())
                .build();

        return response;
    }


    // 예외 처리 - 존재하는 user인지
    private User getUserOrThrow(Long memberId) {

        return userRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }
}
