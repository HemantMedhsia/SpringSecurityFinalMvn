package com.hemant.springsecurityfinalmvn.services.UserService;

import com.hemant.springsecurityfinalmvn.models.UserModel;

import java.util.List;

public interface UserService {
    UserModel findUserById(String id);
    UserModel findUserByEmail(String email);
    List<UserModel> findAllUsers();
}