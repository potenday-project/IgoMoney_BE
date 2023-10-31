package igoMoney.BE.repository;

import igoMoney.BE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByLoginId(String loginId);
    Boolean existsByLoginId(String loginId);
    User findByEmailAndProvider(String email, String provider);
    Boolean existsByNickname(String nickname);
    void deleteById(Long userId);
    List<User> findAllByBanned(Boolean banned);
}
