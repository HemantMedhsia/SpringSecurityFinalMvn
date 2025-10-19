package com.hemant.springsecurityfinalmvn.services.AuthService;

import org.springframework.http.ResponseEntity;

import com.hemant.springsecurityfinalmvn.dtos.auth.AuthRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.RegisterRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.SaveUserResponse;
import com.hemant.springsecurityfinalmvn.dtos.user.UserResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;

public interface AuthService {
    SaveUserResponse saveUser(RegisterRequest user);
    ResponseEntity<ResponseStructure<UserResponseDto>> loginUser(AuthRequest req);
}

