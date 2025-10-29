package com.hemant.springsecurityfinalmvn.services.expense;

import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    ResponseEntity<ResponseStructure<ExpenseResponseDto>> createExpense(AddExpenseDto expenseDto);
    ResponseEntity<ResponseStructure<ExpenseResponseDto>> updateExpense(Long id, AddExpenseDto updatedExpense);
    ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteExpense(Long id);
    ResponseEntity<ResponseStructure<ExpenseResponseDto>> getExpenseById(Long id);
    ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getAllExpenses();
    ResponseEntity<ResponseStructure<Double>> getTotalSpent();

}
