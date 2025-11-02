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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        ExpenseResponseDto responseDto = mapToDto(savedExpense);
        return ApiResponse.success(responseDto, "Expense created successfully", HttpStatus.CREATED);
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
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteExpense(Long id) {
        UserModel currentUser = getCurrentUser();
        ExpenseModel expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        if (!expense.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this expense");
        }

        expenseRepository.delete(expense);
        return ApiResponse.success(null, "Expense deleted successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> getExpenseById(Long id) {
        UserModel currentUser = getCurrentUser();
        ExpenseModel expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));

        if (!expense.getOwner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to view this expense");
        }

        ExpenseResponseDto responseDto = mapToDto(expense);
        return ApiResponse.success(responseDto, "Expense fetched successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getAllExpenses() {
        UserModel currentUser = getCurrentUser();

        List<ExpenseModel> expenses = expenseRepository.findByOwnerId((currentUser.getId()));
        List<ExpenseResponseDto> expenseDtos = expenses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ApiResponse.success(expenseDtos, "All expenses fetched successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Double>> getTotalSpent() {
        UserModel currentUser = getCurrentUser();

        List<ExpenseModel> expenses = expenseRepository.findByOwnerId(currentUser.getId());
        double total = expenses.stream()
                .mapToDouble(ExpenseModel::getAmount)
                .sum();

        return ApiResponse.success(total, "Total spent amount fetched successfully", HttpStatus.OK);
    }

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
