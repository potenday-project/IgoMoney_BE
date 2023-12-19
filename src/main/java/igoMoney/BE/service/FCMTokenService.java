package igoMoney.BE.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import igoMoney.BE.common.exception.CustomException;
import igoMoney.BE.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

@Repository
@RequiredArgsConstructor
public class FCMTokenService {

    private final StringRedisTemplate tokenRedisTemplate;

    public void sendNotification(String title, String content, Long userId) {
        if(!hasKey(userId)){
            throw new CustomException(ErrorCode.FCM_TOKEN_INVALID);
        }
        String token = getToken(userId);
        Notification fcmNotification = Notification.builder()
                .setTitle(title)
                .setBody(content)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(fcmNotification)
                .putData("userId", String.valueOf(userId))
                .build();
        sendMessage(message);
    }

    public void saveToken(Long userId, String FCMToken){
        tokenRedisTemplate.opsForValue()
                .set(String.valueOf(userId), FCMToken);
    }

    private String getToken(Long userId) {
        return tokenRedisTemplate.opsForValue().get(String.valueOf(userId));
    }

    public void deleteToken(Long userId) {
        tokenRedisTemplate.delete(String.valueOf(userId));
    }

    public boolean hasKey(Long userId){
        return tokenRedisTemplate.hasKey(String.valueOf(userId));
    }

    public void sendMessage(Message message) {
        try {
            FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.FCM_MESSAGE_FAILED);
        }
    }
}
