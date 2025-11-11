package com.hemant.springsecurityfinalmvn.services.AuthService;

import com.hemant.springsecurityfinalmvn.dtos.auth.ForgotPasswordRequest;
import com.hemant.springsecurityfinalmvn.dtos.auth.ResetPasswordRequest;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import org.springframework.http.ResponseEntity;

public interface ForgetPasswordService {
    ResponseEntity<ResponseStructure<String>> generateTokenAndSendEmail(ForgotPasswordRequest request);
    ResponseEntity<ResponseStructure<String>> resetPassword(ResetPasswordRequest request);
}
