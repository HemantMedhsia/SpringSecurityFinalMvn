package com.hemant.springsecurityfinalmvn.dtos.auth;

import com.hemant.springsecurityfinalmvn.models.UserModel;

public record SaveUserResponse(
        UserModel user,
        TokenResponse tokenResponse
) {
}
