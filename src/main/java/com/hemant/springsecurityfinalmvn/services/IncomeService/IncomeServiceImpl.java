package com.hemant.springsecurityfinalmvn.services.IncomeService;

import com.hemant.springsecurityfinalmvn.dtos.income.IncomeRequest;
import com.hemant.springsecurityfinalmvn.dtos.income.IncomeResponse;
import com.hemant.springsecurityfinalmvn.models.IncomeModel;
import com.hemant.springsecurityfinalmvn.models.ResponseStructure;
import com.hemant.springsecurityfinalmvn.models.UserModel;

import com.hemant.springsecurityfinalmvn.repos.IncomeRepository;
import com.hemant.springsecurityfinalmvn.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;
    private final UserRepo userRepository;

    @Override
    public IncomeResponse addIncome(IncomeRequest request) {
        UserModel owner = userRepository.findById(String.valueOf(request.userId()))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.userId()));

        IncomeModel income = IncomeModel.builder()
                .source(request.source())
                .amount(request.amount())
                .date(request.date())
                .description(request.description())
                .fileUrl(request.fileUrl())
                .icon(request.icon())
                .owner(owner)
                .build();

        IncomeModel saved = incomeRepository.save(income);
        return toResponse(saved);
    }

    @Override
    public List<IncomeResponse> getAllIncomesByUser(Long userId) {
        UserModel owner = userRepository.findById(String.valueOf(userId))
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return incomeRepository.findByOwner(owner)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public IncomeResponse updateIncome(Long id, IncomeRequest request) {
        IncomeModel income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found with ID: " + id));

        income.setSource(request.source());
        income.setAmount(request.amount());
        income.setDate(request.date());
        income.setDescription(request.description());
        income.setFileUrl(request.fileUrl());
        income.setIcon(request.icon());

        IncomeModel updated = incomeRepository.save(income);
        return toResponse(updated);
    }

    @Override
    public void deleteIncome(Long id) {
        if (!incomeRepository.existsById(id)) {
            throw new RuntimeException("Income not found with ID: " + id);
        }
        incomeRepository.deleteById(id);
    }

    @Override
    public IncomeResponse getIncomeById(Long id) {
        IncomeModel income = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found with ID: " + id));
        return toResponse(income);
    }

    @Override
    public ResponseEntity<ResponseStructure<Double>> getTotalSpent() {
        return null;
    }

    private IncomeResponse toResponse(IncomeModel income) {
        return new IncomeResponse(
                income.getId(),
                income.getSource(),
                income.getAmount(),
                income.getDate(),
                income.getDescription(),
                income.getFileUrl(),
                income.getIcon(),
                income.getOwner().getEmail()
        );
    }
}
