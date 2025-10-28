package com.hemant.springsecurityfinalmvn.services.expense;

import com.hemant.springsecurityfinalmvn.dtos.common.ApiResponse;
import com.hemant.springsecurityfinalmvn.dtos.expense.AddExpenseDto;
import com.hemant.springsecurityfinalmvn.dtos.expense.ExpenseResponseDto;
import com.hemant.springsecurityfinalmvn.models.ExpenseModel;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import com.hemant.springsecurityfinalmvn.repos.ExpenseRepository;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepo repo;
    private final ExpenseRepository expenseRepository;
    
    private UserModel getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> createExpense(AddExpenseDto expenseDto) {
    	UserModel currentUser = getCurrentUser();

        ExpenseModel expense = ExpenseModel.builder()
                .title(expenseDto.title())
                .amount(expenseDto.amount())
                .category(expenseDto.category())
                .description(expenseDto.description())
                .date(expenseDto.date() != null ? expenseDto.date() : LocalDate.now())
                .fileUrl(expenseDto.fileUrl())
                .icon(expenseDto.icon())
                .owner(currentUser)
                .build();

        ExpenseModel savedExpense = expenseRepository.save(expense);

        ExpenseResponseDto responseDto = new ExpenseResponseDto(
                savedExpense.getId(),
                savedExpense.getTitle(),
                savedExpense.getAmount(),
                savedExpense.getCategory(),
                savedExpense.getDescription(),
                savedExpense.getDate(),
                savedExpense.getFileUrl(),
                savedExpense.getIcon(),
                new ExpenseResponseDto.OwnerInfo(
                        currentUser.getId(),
                        currentUser.getName()
                )
        );

       
        return  ApiResponse.success(responseDto, "creted successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> updateExpense(Long id, AddExpenseDto updatedExpense) {
        UserModel currentUser = getCurrentUser();

        ExpenseModel expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        if (!expense.getOwner().getId().equals(currentUser.getId())) {
        	throw new AccessDeniedException("You are not authorized to modify this expense");
        }

        expense.setTitle(updatedExpense.title());
        expense.setAmount(updatedExpense.amount());
        expense.setCategory(updatedExpense.category());
        expense.setDescription(updatedExpense.description());
        expense.setDate(updatedExpense.date() != null ? updatedExpense.date() : LocalDate.now());
        expense.setFileUrl(updatedExpense.fileUrl());
        expense.setIcon(updatedExpense.icon());

        ExpenseModel updated = expenseRepository.save(expense);
        ExpenseResponseDto responseDto = mapToDto(updated);

        return ApiResponse.success(responseDto, "Expense updated successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<String>> deleteExpense(Long id) { return null; }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> getExpenseById(Long id) { return null; }

    @Override
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getAllExpenses() { return null; }

    @Override
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getExpensesByCategory(String category) { return null; }

    @Override
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getExpensesBetweenDates(LocalDate startDate, LocalDate endDate) { return null; }

    @Override
    public ResponseEntity<ResponseStructure<Double>> getTotalSpent() { return null; }

    @Override
    public ResponseEntity<ResponseStructure<Object>> getCategoryWiseSummary() { return null; }
    
    private ExpenseResponseDto mapToDto(ExpenseModel e) {
        return new ExpenseResponseDto(
                e.getId(),
                e.getTitle(),
                e.getAmount(),
                e.getCategory(),
                e.getDescription(),
                e.getDate(),
                e.getFileUrl(),
                e.getIcon(),
                new ExpenseResponseDto.OwnerInfo(
                        e.getOwner().getId(),
                        e.getOwner().getName()
                )
        );
    }
}
