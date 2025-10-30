package com.hemant.springsecurityfinalmvn.services.SavingService;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.hemant.springsecurityfinalmvn.dtos.saving.AddSavingDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.SavingResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;

public interface SavingService {
	
	ResponseEntity<ResponseStructure<SavingResponseDto>> createSaving(AddSavingDto savingDto);
	ResponseEntity<ResponseStructure<SavingResponseDto>> getSavingById(Long id);
	ResponseEntity<ResponseStructure<List<SavingResponseDto>>> getAllSaving();
	ResponseEntity<ResponseStructure<SavingResponseDto>> deleteSaving(Long id);
	ResponseEntity<ResponseStructure<SavingResponseDto>> updateSaving(Long id, AddSavingDto savingDto);
	ResponseEntity<ResponseStructure<Double>> getTotalSaving();
	ResponseEntity<ResponseStructure<Map<String,Double>>> getSavingByMonth();
	ResponseEntity<ResponseStructure<Double>> getSavingSumByMonth(String month);

	
	

}
