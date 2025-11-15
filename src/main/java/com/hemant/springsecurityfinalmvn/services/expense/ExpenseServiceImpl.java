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
import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepo userRepo;
    private final ExpenseRepository expenseRepository;

    private UserModel getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private void verifyOwnership(ExpenseModel expense, UserModel user) {
        if (!expense.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not allowed");
        }
    }

    private ExpenseModel buildExpense(AddExpenseDto dto, UserModel owner) {
        return ExpenseModel.builder()
                .title(dto.title())
                .amount(dto.amount())
                .category(dto.category())
                .description(dto.description())
                .date(dto.date() != null ? dto.date() : LocalDate.now())
                .fileUrl(dto.fileUrl())
                .icon(dto.icon())
                .owner(owner)
                .build();
    }

    private ExpenseResponseDto toDto(ExpenseModel e) {
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

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> createExpense(AddExpenseDto dto) {
        UserModel user = getCurrentUser();
        ExpenseModel saved = expenseRepository.save(buildExpense(dto, user));
        return ApiResponse.success(toDto(saved), "Expense created successfully", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> updateExpense(Long id, AddExpenseDto dto) {
        UserModel user = getCurrentUser();
        ExpenseModel expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        verifyOwnership(expense, user);

        expense.setTitle(dto.title());
        expense.setAmount(dto.amount());
        expense.setCategory(dto.category());
        expense.setDescription(dto.description());
        expense.setDate(dto.date() != null ? dto.date() : LocalDate.now());
        expense.setFileUrl(dto.fileUrl());
        expense.setIcon(dto.icon());

        ExpenseModel updated = expenseRepository.save(expense);
        return ApiResponse.success(toDto(updated), "Expense updated successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> deleteExpense(Long id) {
        UserModel user = getCurrentUser();
        ExpenseModel expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        verifyOwnership(expense, user);

        expenseRepository.delete(expense);
        return ApiResponse.success(null, "Expense deleted successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> getExpenseById(Long id) {
        UserModel user = getCurrentUser();
        ExpenseModel expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        verifyOwnership(expense, user);

        return ApiResponse.success(toDto(expense), "Expense fetched successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getAllExpenses() {
        UserModel user = getCurrentUser();
        List<ExpenseResponseDto> list = expenseRepository
                .findByOwnerId(user.getId())
                .stream()
                .map(this::toDto)
                .toList();

        return ApiResponse.success(list, "All expenses fetched successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Double>> getTotalSpent() {
        UserModel user = getCurrentUser();
        double total = expenseRepository.findByOwnerId(user.getId())
                .stream()
                .mapToDouble(ExpenseModel::getAmount)
                .sum();

        return ApiResponse.success(total, "Total spent amount fetched successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<ExpenseResponseDto>>> getCurrentMonthExpenses() {
        UserModel user = getCurrentUser();

        List<ExpenseResponseDto> list = expenseRepository
                .findCurrentMonthExpenses(user.getId())
                .stream()
                .map(this::toDto)
                .toList();

        return ApiResponse.success(list, "Current month expenses fetched successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<Double>> getCurrentMonthTotal() {
        UserModel user = getCurrentUser();
        Double total = expenseRepository.findCurrentMonthTotal(user.getId());
        return ApiResponse.success(total != null ? total : 0.0, "Fetched", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<Map<String, Object>>>> getCategoryTotals() {
        UserModel user = getCurrentUser();

        List<Object[]> raw = expenseRepository.findCategoryTotals(user.getId());

        List<Map<String, Object>> result = raw.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", r[0]);
            map.put("value", r[1]);
            return map;
        }).toList();

        return ApiResponse.success(result, "Category totals fetched", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStructure<List<Map<String, Object>>>> getLast6MonthsTrend() {
        UserModel user = getCurrentUser();

        LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6).withDayOfMonth(1);

        List<Object[]> raw = expenseRepository.findLast6Months(user.getId(), sixMonthsAgo);

        List<Map<String, Object>> result = raw.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", Month.of((int) r[0]).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            map.put("expense", r[1]);
            return map;
        }).toList();

        return ApiResponse.success(result, "Trend fetched", HttpStatus.OK);
    }



}
