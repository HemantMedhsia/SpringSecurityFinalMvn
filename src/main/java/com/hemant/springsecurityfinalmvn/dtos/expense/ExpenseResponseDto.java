package com.hemant.springsecurityfinalmvn.dtos.expense;

import java.time.LocalDate;

public record ExpenseResponseDto(
        Long id,
        String title,
        Double amount,
        String category,
        String description,
        LocalDate date,
        String fileUrl,
        String icon,
        OwnerInfo owner
) {
    public record OwnerInfo(String id, String name) {}
}
