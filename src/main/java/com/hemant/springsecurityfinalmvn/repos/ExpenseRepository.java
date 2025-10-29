package com.hemant.springsecurityfinalmvn.repos;

import com.hemant.springsecurityfinalmvn.models.ExpenseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseModel, Long> {
    List<ExpenseModel> findByOwnerId(String ownerId);
}
