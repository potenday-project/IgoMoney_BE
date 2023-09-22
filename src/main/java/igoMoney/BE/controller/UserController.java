package igoMoney.BE.controller;

import igoMoney.BE.dto.request.UserUpdateRequest;
import igoMoney.BE.dto.response.UserResponse;
import igoMoney.BE.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

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

    // 닉네임 중복 조회
    @GetMapping("nickname/{nickname}")
    public ResponseEntity<Void> checkNicknameDuplicate(@PathVariable("nickname") String nickname) {

        userService.checkNicknameDuplicate(nickname);

        return new ResponseEntity(HttpStatus.OK);
    }

    // 회원정보 수정
    @PatchMapping("")
    public ResponseEntity<Void> updateUser(UserUpdateRequest request) throws IOException {

        userService.updateUser(request);

        return new ResponseEntity(HttpStatus.OK);
    }
}
