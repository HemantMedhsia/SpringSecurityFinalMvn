package com.hemant.springsecurityfinalmvn.controllers.income;

import com.hemant.springsecurityfinalmvn.dtos.income.IncomeRequest;
import com.hemant.springsecurityfinalmvn.dtos.income.IncomeResponse;
import com.hemant.springsecurityfinalmvn.services.IncomeService.IncomeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> addIncome(@Valid @RequestBody IncomeRequest request) {
        IncomeResponse response = incomeService.addIncome(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IncomeResponse>> getAllByUser(@PathVariable Long userId) {
        List<IncomeResponse> incomes = incomeService.getAllIncomesByUser(userId);
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getIncomeById(@PathVariable Long id) {
        IncomeResponse response = incomeService.getIncomeById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request
    ) {
        IncomeResponse response = incomeService.updateIncome(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
