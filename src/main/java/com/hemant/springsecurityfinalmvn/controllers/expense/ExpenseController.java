package com.hemant.springsecurityfinalmvn.controllers.expense;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.services.expense.ExpenseService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/create-expense")
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> createExpense(@RequestBody AddExpenseDto expense) {
        return expenseService.createExpense(expense);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> updateExpense(
            @PathVariable Long id,
            @RequestBody AddExpenseDto updatedExpense
    ) {
        return expenseService.updateExpense(id, updatedExpense);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteExpense(
            @PathVariable Long id
    ) {
        return expenseService.deleteExpense(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> getExpenseById(
            @PathVariable Long id
    ) {
        return expenseService.getExpenseById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/total")
    public ResponseEntity<ResponseStructure<Double>> getTotalSpent() {
        return expenseService.getTotalSpent();
    }
}
