package igoMoney.BE.controller;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import igoMoney.BE.service.FCMTokenService;
import igoMoney.BE.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("fcm")
public class FCMMessageController {

    private final FCMTokenService fcmTokenService;
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<Void> sendPushAlarm(@RequestParam(value="fcmToken") String fcmToken,
                                        @RequestParam(value="title") String title,
                                        @RequestParam(value="content") String content){

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(content)
                        .build())
                .setToken(fcmToken)
                .build();
        fcmTokenService.sendMessage(message);
        return new ResponseEntity(HttpStatus.OK);
    }
}
