package com.hemant.springsecurityfinalmvn.dtos.auth;

public record TokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long expiresInSeconds
) {}
