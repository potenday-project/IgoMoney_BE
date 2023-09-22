package igoMoney.BE.service;

import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import igoMoney.BE.domain.User;
import igoMoney.BE.dto.request.UserUpdateRequest;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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

    // 닉네임 중복 조회
    @Transactional(readOnly = true)
    public void checkNicknameDuplicate(String nickname) {

        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.EXIST_MEMBER_NICKNAME);
        }
    }

    // 닉네임 변경하기 (회원가입 시 애플은 빈칸으로 가입됨. 닉네임 설정 후 홈화면 나옴)
    public void updateUser(UserUpdateRequest request) throws IOException {

        User findUser = getUserOrThrow(request.getId());

        if (!request.getNickname().equals(findUser.getNickname())) {
            checkNicknameDuplicate(request.getNickname());
        }

        findUser.updateUser(request);
    }

    // 예외 처리 - 존재하는 user인지
    private User getUserOrThrow(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }
}
