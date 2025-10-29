package com.hemant.springsecurityfinalmvn.services.SavingService;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.AddSavingDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.SavingResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;

public interface SavingService {
	
	ResponseEntity<ResponseStructure<SavingResponseDto>> createSaving(AddSavingDto savingDto);
	ResponseEntity<ResponseStructure<SavingResponseDto>> getSavingById(Long id);
	ResponseEntity<ResponseStructure<List<SavingResponseDto>>> getAllSaving();
	ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteSaving();
	ResponseEntity<ResponseStructure<SavingResponseDto>> updateSaving(Long id, AddSavingDto savingDto);
	

}
