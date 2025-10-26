package com.hemant.springsecurityfinalmvn.controllers.auth;

import com.hemant.springsecurityfinalmvn.dtos.auth.AuthRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.RegisterRequest;
import com.hemant.springsecurityfinalmvn.dtos.user.UserResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.services.AuthService.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseStructure<UserResponseDto>> registerUser(@RequestBody RegisterRequest user) {
        return authService.saveUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<UserResponseDto>> login(@Valid @RequestBody AuthRequest req) {
        return authService.loginUser(req);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseStructure<Object>> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        return authService.refreshAccessToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return authService.logoutUser();
    }


}
