package com.hemant.springsecurityfinalmvn.repos;

import com.hemant.springsecurityfinalmvn.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel, String> {
    Optional<UserModel> findByEmail(String email);
}
