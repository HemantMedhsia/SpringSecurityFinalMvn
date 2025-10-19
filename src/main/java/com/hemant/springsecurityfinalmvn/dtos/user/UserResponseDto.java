package com.hemant.springsecurityfinalmvn.dtos.user;

import com.hemant.springsecurityfinalmvn.constants.UserRole;

public record UserResponseDto(
        String id,
        String name,
        String email,
        UserRole role     
) {}