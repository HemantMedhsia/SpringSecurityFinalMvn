package com.hemant.springsecurityfinalmvn.dtos.income;

import java.time.LocalDate;

public record IncomeResponse(
        Long id,
        String source,
        Double amount,
        LocalDate date,
        String description,
        String fileUrl,
        String icon,
        String ownerEmail
) {}
