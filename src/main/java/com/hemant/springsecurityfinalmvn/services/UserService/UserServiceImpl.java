package com.hemant.springsecurityfinalmvn.services.UserService;

import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

    @Override
    public UserModel
    findUserById(String id) {
        return userRepo.findById(id).orElse(null);
    }

    @Override
    public UserModel findUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Override
    public List<UserModel> findAllUsers() {
        return userRepo.findAll();
    }
}