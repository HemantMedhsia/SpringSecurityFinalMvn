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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final UserRepo repo;
    private final ExpenseRepository expenseRepository;

    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> createExpense(AddExpenseDto expenseDto) {
        // 1️⃣ Get the currently logged-in user
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserModel currentUser = repo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // 2️⃣ Map DTO → Entity
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

        // 3️⃣ Save the expense to DB
        ExpenseModel savedExpense = expenseRepository.save(expense);

        // 4️⃣ Map Entity → Response DTO
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

    // Other methods (implement later)
    @Override
    public ResponseEntity<ResponseStructure<ExpenseResponseDto>> updateExpense(Long id, AddExpenseDto updatedExpense) { return null; }

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
}
