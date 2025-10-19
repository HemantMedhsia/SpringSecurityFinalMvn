package com.hemant.springsecurityfinalmvn.dtos.common;
import com.hemant.springsecurityfinalmvn.models.ErrorStructure;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class ApiResponse {
    public static <T> ResponseEntity<ResponseStructure<T>> success(T data, String message, HttpStatus statusCode){
        ResponseStructure<T> responseBody = ResponseStructure.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(statusCode).body(responseBody);
    }

    public static ResponseEntity<ErrorStructure> error(String details, String message, HttpStatus statusCode){
        ErrorStructure errorBody = ErrorStructure.builder()
                .status("error")
                .message(message)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(statusCode).body(errorBody);
    }

    /** 🔹 Success + Cookie */
    public static <T> ResponseEntity<ResponseStructure<T>> successWithCookie(
            T data, String message, HttpStatus statusCode, ResponseCookie... cookies) {

        ResponseStructure<T> responseBody = ResponseStructure.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();

        HttpHeaders headers = new HttpHeaders();
        for (ResponseCookie cookie : cookies) {
            headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity.status(statusCode)
                .headers(headers)
                .body(responseBody);
    }

}
