package igoMoney.BE.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import igoMoney.BE.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FCMTokenService {

    private final StringRedisTemplate tokenRedisTemplate;

    public void sendNotification(Notification notification) {
        Long userId = notification.getUser().getId();
        if(!hasKey(userId)){ return;}
        String token = getToken(userId);
        Message message = Message.builder()
                .putData("title", notification.getTitle())
                .putData("content", notification.getMessage())
                .putData("userId", String.valueOf(userId))
                .setToken(token)
                .build();
        sendMessage(message);
    }

    public void saveToken(Long userId, String FCMToken){
        tokenRedisTemplate.opsForValue()
                .set(String.valueOf(userId), FCMToken);
    }

    private String getToken(Long userId) {
        return tokenRedisTemplate.opsForValue().get(userId);
    }

    public void deleteToken(Long userId) {
        tokenRedisTemplate.delete(String.valueOf(userId));
    }

    public boolean hasKey(Long userId){
        return tokenRedisTemplate.hasKey(String.valueOf(userId));
    }

    private void sendMessage(Message message) {
        FirebaseMessaging.getInstance().sendAsync(message);
    }
}
