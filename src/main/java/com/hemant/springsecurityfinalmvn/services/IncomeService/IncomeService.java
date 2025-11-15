package com.hemant.springsecurityfinalmvn.services.IncomeService;

import com.hemant.springsecurityfinalmvn.dtos.income.IncomeRequest;
import com.hemant.springsecurityfinalmvn.dtos.income.IncomeResponse;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IncomeService {
    IncomeResponse addIncome(IncomeRequest request);
    List<IncomeResponse> getAllIncomesByUser(Long userId);
    IncomeResponse updateIncome(Long id, IncomeRequest request);
    void deleteIncome(Long id);
    IncomeResponse getIncomeById(Long id);
    ResponseEntity<ResponseStructure<Double>> getTotalSpent();
}