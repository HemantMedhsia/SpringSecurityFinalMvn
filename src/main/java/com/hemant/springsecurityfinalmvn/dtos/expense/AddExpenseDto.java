package com.hemant.springsecurityfinalmvn.dtos.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AddExpenseDto(
        @NotBlank String title,
        @NotNull Double amount,
        @NotBlank String category,
        @NotBlank String description,
        LocalDate date,
        String fileUrl,
        String icon
) {}