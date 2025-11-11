package com.hemant.springsecurityfinalmvn.repos;

import com.hemant.springsecurityfinalmvn.models.IncomeModel;
import com.hemant.springsecurityfinalmvn.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeModel, Long> {
    List<IncomeModel> findByOwner(UserModel owner);
}
