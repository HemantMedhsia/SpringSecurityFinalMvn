package com.hemant.springsecurityfinalmvn.controllers.saving;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.AddSavingDto;
import com.hemant.springsecurityfinalmvn.dtos.saving.SavingResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.services.SavingService.SavingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/saving")
@RequiredArgsConstructor
public class SavingController {
	
	private final SavingService savingService;
	
	
	
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello Hemant! 👋 Your Spring Boot server is up and running!";
    }
    
    @PostMapping("/create")
    public ResponseEntity<ResponseStructure<SavingResponseDto>> registerUser(@RequestBody AddSavingDto saving) {
        return savingService.createSaving(saving);
    }
    
    
    
}
