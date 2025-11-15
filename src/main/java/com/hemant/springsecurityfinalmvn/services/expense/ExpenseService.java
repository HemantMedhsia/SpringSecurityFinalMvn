package com.hemant.springsecurityfinalmvn.services.expense;

import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {

    ResponseEntity<ResponseStructure<ExpenseResponseDto>> createExpense(AddExpenseDto expenseDto);
    ResponseEntity<ResponseStructure<ExpenseResponseDto>> updateExpense(Long id, AddExpenseDto updatedExpense);
    ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteExpense(Long id);
    ResponseEntity<ResponseStructure<ExpenseResponseDto>> getExpenseById(Long id);
    ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getAllExpenses();
    ResponseEntity<ResponseStructure<Double>> getTotalSpent();
    ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getCurrentMonthExpenses();
    ResponseEntity<ResponseStructure<Double>> getCurrentMonthTotal();
    ResponseEntity<ResponseStructure<List<Map<String, Object>>>> getCategoryTotals();
    ResponseEntity<ResponseStructure<List<Map<String, Object>>>> getLast6MonthsTrend();
}
