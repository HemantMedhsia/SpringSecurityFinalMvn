package com.hemant.springsecurityfinalmvn.dtos.saving;

import java.time.LocalDate;

import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto.OwnerInfo;

public record SavingResponseDto(
		Long id,
        String category,
        String source,
        Double savedAmount,
        String icon,
        String fileUrl,
        LocalDate date,
        OwnerInfo owner
		
		) 


{
	public record OwnerInfo(String id, String name) {}
}
