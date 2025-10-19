package com.hemant.springsecurityfinalmvn.controllers.auth;

import com.hemant.springsecurityfinalmvn.config.security.JwtService;
import com.hemant.springsecurityfinalmvn.dtos.auth.AuthRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.RegisterRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.SaveUserResponse;
import com.hemant.springsecurityfinalmvn.dtos.auth.TokenResponse;
import com.hemant.springsecurityfinalmvn.dtos.common.ApiResponse;
import com.hemant.springsecurityfinalmvn.dtos.user.UserResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;
import com.hemant.springsecurityfinalmvn.services.AuthService.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    ResponseEntity<ResponseStructure<SaveUserResponse>> registerUser(@RequestBody RegisterRequest user) {
        SaveUserResponse saveUserResponse = authService.saveUser(user);
        ResponseCookie accessCookie = ResponseCookie.from("access_token", saveUserResponse.tokenResponse().accessToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", saveUserResponse.tokenResponse().refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/v1/auth/refresh")
                .maxAge(Duration.ofDays(7))
                .build();

        return ApiResponse.successWithCookie(
                saveUserResponse,
                "User registered successfully",
                HttpStatus.CREATED,
                accessCookie,
                refreshCookie
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseStructure<UserResponseDto>> login(@Valid @RequestBody AuthRequest req) {
        return authService.loginUser(req);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResponseStructure<Object>> refresh( @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null)
            throw new BadCredentialsException("Missing refresh token cookie");
        Claims claims;
        try {
            claims = jwtService.parseClaims(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (!"refresh".equals(claims.get("type")))
            throw new BadCredentialsException("Only refresh tokens are allowed");

        String username = claims.getSubject();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, user))
            throw new BadCredentialsException("Invalid or expired refresh token");

        String newAccessToken = jwtService.generateAccessToken(user);
        int expirySeconds = 15 * 60;

        ResponseCookie newAccessCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        return ApiResponse.successWithCookie(null, "Access token generated successfully", HttpStatus.CREATED, newAccessCookie);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {

        ResponseCookie clearAccessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false) // true in production
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/v1/auth/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccessCookie.toString(), clearRefreshCookie.toString())
                .body(Map.of("message", "Logout successful"));
    }


}
