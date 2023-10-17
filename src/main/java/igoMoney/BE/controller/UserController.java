package igoMoney.BE.controller;

import igoMoney.BE.dto.request.UserUpdateRequest;
import igoMoney.BE.dto.response.NotificationResponse;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    // 회원 정보 조회
    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {

        UserResponse response = userService.getUser(id);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    // 닉네임 중복 확인
    @GetMapping("nickname/{nickname}")
    public ResponseEntity<Void> checkNicknameDuplicate(@PathVariable("nickname") String nickname) {

        userService.checkNicknameDuplicate(nickname);

        return new ResponseEntity(HttpStatus.OK);
    }

    // 회원정보 수정
    @PatchMapping("")
    public ResponseEntity<Void> updateUser(@Valid UserUpdateRequest request) throws IOException {

        userService.updateUser(request);

        return new ResponseEntity(HttpStatus.OK);
    }

    // 확인 안한 알림 목록 조회
    @GetMapping("notification/{userId}/")
    public ResponseEntity<List<NotificationResponse>> getUncheckedNotificationList(@PathVariable("userId") Long userId){

        List<NotificationResponse> response = userService.getUncheckedNotificationList(userId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 알림 확인 체크
    @PostMapping("notification/check/{notificationId}/")
    public ResponseEntity<Void> checkNotificationToRead(@PathVariable("notificationId") Long notificationId){

        userService.checkNotificationToRead(notificationId);

        return new ResponseEntity(HttpStatus.OK);
    }
}
