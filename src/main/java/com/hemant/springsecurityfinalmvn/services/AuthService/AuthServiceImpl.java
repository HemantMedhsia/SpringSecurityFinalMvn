package com.hemant.springsecurityfinalmvn.services.AuthService;

import com.hemant.springsecurityfinalmvn.config.security.JwtService;
import com.hemant.springsecurityfinalmvn.dtos.auth.AuthRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.RegisterRequest;
import com.hemant.springsecurityfinalmvn.dtos.common.ApiResponse;
import com.hemant.springsecurityfinalmvn.dtos.user.UserResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;
import com.hemant.springsecurityfinalmvn.utils.PasswordEncoder;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepo userRepo;
    private final UserDetailsService userDetailsService;
    private final JwtService  jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public ResponseEntity<ResponseStructure<UserResponseDto>> saveUser(RegisterRequest registerRequest) {

        String email = registerRequest.email().trim().toLowerCase();

        if (userRepo.findByEmail(email).isPresent()) {
            throw new DataIntegrityViolationException("User with email (" + email + ") already exists");
        }

        UserModel newUser = UserModel.builder()
                .name(registerRequest.name().trim())
                .email(email)
                .password(PasswordEncoder.passwordEncoder().encode(registerRequest.password()))
                .role(registerRequest.role())
                .build();

        UserModel savedUser = userRepo.saveAndFlush(newUser);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        savedUser.setRefreshToken(refreshToken);
        userRepo.save(savedUser);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofHours(1))
                .build();

        return ApiResponse.successWithCookie(
                new UserResponseDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole()),
                "User registered successfully",
                HttpStatus.CREATED,
                accessCookie,
                refreshCookie
        );
    }


	@Override
	public ResponseEntity<ResponseStructure<UserResponseDto>> loginUser(AuthRequest req) {
		
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password()));
        } catch (BadCredentialsException e) {
            throw e;
        }

        UserModel fetchUser = userRepo.findByEmail(req.email()
        		.toLowerCase())
        		.orElseThrow(() -> 
        			new UsernameNotFoundException(
                        "Unable to fetch user with email(" + req.email() + ")"
                ));

        UserDetails user = userDetailsService.loadUserByUsername(req.email().toLowerCase());
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", access)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMinutes(1))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMinutes(3))
                .build();

        return ApiResponse.successWithCookie(
        		new UserResponseDto(fetchUser.getId(), fetchUser.getName(), fetchUser.getEmail(), fetchUser.getRole()),
                "User login successfully",
                HttpStatus.OK,
                accessCookie,
                refreshCookie
        );
	}
	

	@Override
    public ResponseEntity<ResponseStructure<Object>> refreshAccessToken(String refreshToken) {

        if (refreshToken == null) {
            throw new BadCredentialsException("Missing refresh token cookie");
        }

        Claims claims;
        try {
            claims = jwtService.parseClaims(refreshToken);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        if (!"refresh".equals(claims.get("type"))) {
            throw new BadCredentialsException("Only refresh tokens are allowed");
        }

        String username = claims.getSubject();
        UserDetails user = userDetailsService.loadUserByUsername(username);
        
        if(user == null) {
        	throw new BadCredentialsException("User not found in the token");
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);

        ResponseCookie newAccessCookie = ResponseCookie.from("access_token", newAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMinutes(15))
                .build();

        return ApiResponse.successWithCookie(
                null,
                "Access token generated successfully",
                HttpStatus.CREATED,
                newAccessCookie
        );
    }

    @Override
    public ResponseEntity<Map<String, String>> logoutUser() {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "No authenticated user found"));
        }

        String email = authentication.getName();

        userRepo.findByEmail(email).ifPresent(user -> {
            user.setRefreshToken(null);
            userRepo.save(user);
        });

    	
        ResponseCookie clearAccessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearRefreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(
                		HttpHeaders.SET_COOKIE, 
                		clearAccessCookie.toString(), 
                		clearRefreshCookie.toString())
                .body(
                		Map.of(
                				"message", 
                				"Logout successful"
                				)
                		);
    }
}
