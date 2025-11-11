package com.hemant.springsecurityfinalmvn.repos;

import com.hemant.springsecurityfinalmvn.models.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
    void deleteByToken(String token);
    void deleteAllByExpiryTimeBefore(LocalDateTime time);


}