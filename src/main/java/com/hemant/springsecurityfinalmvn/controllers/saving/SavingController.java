package com.hemant.springsecurityfinalmvn.controllers.saving;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<SavingResponseDto>> getSavingById(@PathVariable Long id){
    	return savingService.getSavingById(id);
    	
    }
    
    @GetMapping("/allsaving")
    public ResponseEntity<ResponseStructure<List<SavingResponseDto>>> getAllSaving (){
    	return savingService.getAllSaving();
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<SavingResponseDto>> deleteExpense(
            @PathVariable Long id
    ) {
        return savingService.deleteSaving(id);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseStructure<SavingResponseDto>> updateSaving(
            @PathVariable Long id,
            @RequestBody AddSavingDto updatedSaving
    ) {
        return savingService.updateSaving(id, updatedSaving);
    }
    
    @GetMapping("/total")
    public ResponseEntity<ResponseStructure<Double>> getTotalSaving() {
        return savingService.getTotalSaving();
    }

    @GetMapping("/monthly")
    public ResponseEntity<ResponseStructure<Map<String, Double>>> getSavingByMonth() {
        return savingService.getSavingByMonth();
    }

    
    @GetMapping("/month/{monthName}")
    public ResponseEntity<ResponseStructure<Double>> getSavingSumByMonth(@PathVariable String monthName) {
        return savingService.getSavingSumByMonth(monthName);
    }


    
}
