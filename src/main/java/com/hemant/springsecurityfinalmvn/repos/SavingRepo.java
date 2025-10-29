package com.hemant.springsecurityfinalmvn.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hemant.springsecurityfinalmvn.models.SavingsModel;

public interface SavingRepo extends JpaRepository<SavingsModel,Long >  {

}
