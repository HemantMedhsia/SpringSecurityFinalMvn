package com.hemant.springsecurityfinalmvn.services.AuthService;

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
import com.hemant.springsecurityfinalmvn.utils.PasswordEncoder;
import lombok.AllArgsConstructor;

import java.time.Duration;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepo userRepo;
    private final UserDetailsService userDetailsService;
    private final JwtService  jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public SaveUserResponse saveUser(RegisterRequest registerRequest) {
        return getSaveUserResponse(registerRequest, userRepo, userDetailsService, jwtService);
    }

    static SaveUserResponse getSaveUserResponse(RegisterRequest registerRequest,
                                                UserRepo userRepo,
                                                UserDetailsService userDetailsService,
                                                JwtService jwtService) {
        if(userRepo.findByEmail(registerRequest.email()).isPresent())
            throw new DataIntegrityViolationException("User with email(" +  registerRequest.email() + ") already exists");
        UserModel userModel = UserModel.builder()
                .name(registerRequest.name())
                .email(registerRequest.email().toLowerCase())
                .password(PasswordEncoder.passwordEncoder().encode(registerRequest.password()))
                .role(registerRequest.role())
                .refreshToken("")
                .build();
        try {
            UserModel savedUser = userRepo.save(userModel);
            UserDetails userDetails = userDetailsService.loadUserByUsername(registerRequest.email());
            String access = jwtService.generateAccessToken(userDetails);
            String refresh = jwtService.generateRefreshToken(userDetails);
            savedUser.setRefreshToken(refresh);
            UserModel savedUserModel = userRepo.save(savedUser);
            return new SaveUserResponse(savedUserModel,new TokenResponse("Bearer", access, refresh, 15 * 60));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public ResponseEntity<ResponseStructure<UserResponseDto>> loginUser(AuthRequest req) {
		try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password()));
        } catch (BadCredentialsException e) {
            throw e;
        }

        UserModel fetchUser = userRepo.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Unable to fetch user with email(" + req.email() + ")"
                ));

        UserDetails user = userDetailsService.loadUserByUsername(req.email().toLowerCase());
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        ResponseCookie accessCookie = ResponseCookie.from("access_token", access)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofMinutes(1))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refresh)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/api/v1/auth/refresh")
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
}
