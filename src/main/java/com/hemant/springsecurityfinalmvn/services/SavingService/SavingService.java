package com.hemant.springsecurityfinalmvn.services.SavingService;

import org.springframework.http.ResponseEntity;

import com.hemant.springsecurityfinalmvn.dtos.saving.AddSavingDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.SavingResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;

public interface SavingService {
	
	ResponseEntity<ResponseStructure<SavingResponseDto>> createSaving(AddSavingDto savingDto);

}
