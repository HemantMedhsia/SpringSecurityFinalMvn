package com.hemant.springsecurityfinalmvn.services.SavingService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
	
	
	 private UserModel getCurrentUser() {
	        String email = SecurityContextHolder.getContext().getAuthentication().getName();
	        return userRepo.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	    }
	
	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> createSaving(AddSavingDto savingDto) {
		
		UserModel currentUser = getCurrentUser();

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
	
	
	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> getSavingById(Long id){
		UserModel currentUser = getCurrentUser();
		
		SavingsModel saving = savingRepo.findById(id)
				.orElseThrow(()-> new RuntimeException("Saving not found with id "+id));
		
		 if (!saving.getOwner().getId().equals(currentUser.getId())) {
	            throw new AccessDeniedException("You are not authorized to view this saving");
	        }

	        SavingResponseDto responseDto = mapToDto(saving);
	        return ApiResponse.success(responseDto, "Expense fetched successfully", HttpStatus.OK);
		
		
		
	}
	
	@Override
	public ResponseEntity<ResponseStructure<List<SavingResponseDto>>> getAllSaving(){
		UserModel currentUser = getCurrentUser();
		
		List<SavingsModel> saving = savingRepo.findByOwnerId(currentUser.getId());
		
		 List<SavingResponseDto> savingDto = saving.stream()                                     
		         .map(this::mapToDto)                                                                 
		         .collect(Collectors.toList());                                                       
		                                                                                              
		 return ApiResponse.success(savingDto, "All Saving fetched successfully", HttpStatus.OK); 
		                                                                                              			
	}

	@Override
	public ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteSaving() {
		return null;
	}

	@Override
	public ResponseEntity<ResponseStructure<SavingResponseDto>> updateSaving(Long id, AddSavingDto savingDto) {
		return null;
	}


	private SavingResponseDto mapToDto(SavingsModel s) {
	    return new SavingResponseDto(
	            s.getId(),
	            s.getCategory(),
	            s.getSource(),
	            s.getSavedAmount(),
	            s.getIcon(),
	            s.getFileUrl(),
	            s.getDate(),
	            new SavingResponseDto.OwnerInfo(
	                    s.getOwner().getId().toString(),
	                    s.getOwner().getName()
	            )
	    );
	}



}
