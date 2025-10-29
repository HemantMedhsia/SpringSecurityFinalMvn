package com.hemant.springsecurityfinalmvn.dtos.saving;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddSavingDto(
		@NotBlank String category,
		@NotBlank String source,
		@NotNull Double savedAmount,
		String icon,
		String fileUrl,
		LocalDate date
		) 
{}
