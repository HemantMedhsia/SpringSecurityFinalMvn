package com.hemant.springsecurityfinalmvn.dtos.income;

import java.time.LocalDate;
import java.util.UUID;

public record IncomeRequest(
        String source,
        Double amount,
        LocalDate date,
        String description,
        String fileUrl,
        String icon,
        UUID userId
) {}
