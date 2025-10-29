package com.hemant.springsecurityfinalmvn.services.SavingService;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hemant.springsecurityfinalmvn.dtos.common.ApiResponse;
import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.AddSavingDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.SavingResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.SavingsModel;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.ExpenseRepository;
import com.hemant.springsecurityfinalmvn.repos.SavingRepo;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SavingServiceImpl implements SavingService {
	
	private final UserRepo userRepo;
	private final SavingRepo savingRepo;
	
	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> createSaving(AddSavingDto savingDto) {
		
	    String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
	    UserModel currentUser = userRepo.findByEmail(userEmail)
	            .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

	    SavingsModel saving = SavingsModel.builder()
	            .category(savingDto.category())
	            .source(savingDto.source())
	            .savedAmount(savingDto.savedAmount())
	            .icon(savingDto.icon())
	            .fileUrl(savingDto.fileUrl())
	            .date(savingDto.date() != null ? savingDto.date() : LocalDate.now()) 
	            .owner(currentUser)
	            .build();

	    SavingsModel savedSaving = savingRepo.save(saving);

	    SavingResponseDto responseDto = new SavingResponseDto(
	            savedSaving.getId(),
	            savedSaving.getCategory(),
	            savedSaving.getSource(),
	            savedSaving.getSavedAmount(),
	            savedSaving.getIcon(),
	            savedSaving.getFileUrl(),
	            savedSaving.getDate(),
	            new SavingResponseDto.OwnerInfo(
	                    currentUser.getId().toString(),
	                    currentUser.getName()
	            )
	    );

	    return ApiResponse.success(responseDto, "Saving created successfully", HttpStatus.CREATED);
	}


}
