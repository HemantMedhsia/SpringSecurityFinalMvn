package com.hemant.springsecurityfinalmvn.controllers.expense;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.services.expense.ExpenseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/expense")
@RequiredArgsConstructor
public class ExpenseController {
	
	private final ExpenseService expenseService;
	
	@PostMapping("/create-expense")
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> registerUser(@RequestBody AddExpenseDto expense) {
        return expenseService.createExpense(expense);
    }
}
