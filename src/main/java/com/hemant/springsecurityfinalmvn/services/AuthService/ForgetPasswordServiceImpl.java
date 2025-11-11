package com.hemant.springsecurityfinalmvn.services.AuthService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.hemant.springsecurityfinalmvn.dtos.auth.ForgotPasswordRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.ResetPasswordRequest;
import com.hemant.springsecurityfinalmvn.models.PasswordReset;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.ForgetPasswordRepository;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;
import com.hemant.springsecurityfinalmvn.utils.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;




import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForgetPasswordServiceImpl implements ForgetPasswordService {

    private final UserRepo userRepository;
    private final ForgetPasswordRepository forgetPassRepo;
    private final JavaMailSender mailSender;



    private final long TOKEN_EXPIRY_MINUTES = 15;


    @Override
    public ResponseEntity<ResponseStructure<String>> generateTokenAndSendEmail(ForgotPasswordRequest request){

        ResponseStructure<String> response = new ResponseStructure<>();

        Optional<UserModel> userOpt = userRepository.findByEmail(request.email());
        if (userOpt.isEmpty()) {
            response.setStatus("SUCCESS");
            response.setMessage("If an account with that email exists, a reset link was sent.");
            response.setData(null);
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.ok(response);
        }

        UserModel user = userOpt.get();


        String token = UUID.randomUUID().toString();


        PasswordReset passwordReset = PasswordReset.builder()
                .token(token)
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES))
                .build();

        forgetPassRepo.save(passwordReset);


        sendResetEmail(user.getEmail(), token);


        response.setStatus("SUCCESS");
        response.setMessage("Password reset link sent successfully.");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);


    }


    @Override
    public ResponseEntity<ResponseStructure<String>> resetPassword(ResetPasswordRequest request){

        ResponseStructure<String> response = new ResponseStructure<>();

        Optional<PasswordReset> tokenOpt = forgetPassRepo.findByToken(request.token());
        if (tokenOpt.isEmpty()) {
            response.setStatus("FAILED");
            response.setMessage("Invalid token.");
            response.setData(null);
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        }

        PasswordReset tokenEntity = tokenOpt.get();


        if (tokenEntity.getExpiryTime().isBefore(LocalDateTime.now())) {
            forgetPassRepo.delete(tokenEntity);
            response.setStatus("FAILED");
            response.setMessage("Token expired. Please request again.");
            response.setData(null);
            response.setTimestamp(LocalDateTime.now());
            return ResponseEntity.badRequest().body(response);
        }


        UserModel user = tokenEntity.getUser();
        user.setPassword(PasswordEncoder.passwordEncoder().encode(request.password()));
        userRepository.save(user);


        forgetPassRepo.delete(tokenEntity);


        response.setStatus("SUCCESS");
        response.setMessage("Password reset successfully.");
        response.setData(null);
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);

    }


    @Async
    protected void sendResetEmail(String to, String token) {
        String resetLink = "http://localhost:5173/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");
        message.setText("We received a request to reset your password.\n\n" +
                "Click the link below to reset it:\n" +
                resetLink + "\n\nThis link will expire in " + TOKEN_EXPIRY_MINUTES + " minutes.\n\n" +
                "If you did not request this, please ignore this email.");

        mailSender.send(message);
    }


}

