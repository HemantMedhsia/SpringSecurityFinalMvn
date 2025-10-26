package com.hemant.springsecurityfinalmvn.services.AuthService;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.hemant.springsecurityfinalmvn.dtos.auth.AuthRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.RegisterRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.SaveUserResponse;
import com.hemant.springsecurityfinalmvn.dtos.user.UserResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;

public interface AuthService {
    ResponseEntity<ResponseStructure<UserResponseDto>> saveUser(RegisterRequest user);
    ResponseEntity<ResponseStructure<UserResponseDto>> loginUser(AuthRequest req);
    ResponseEntity<ResponseStructure<Object>> refreshAccessToken(String refreshToken);
    ResponseEntity<Map<String, String>> logoutUser();
}

