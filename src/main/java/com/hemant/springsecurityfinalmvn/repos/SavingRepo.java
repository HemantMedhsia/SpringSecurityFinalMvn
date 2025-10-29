package com.hemant.springsecurityfinalmvn.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hemant.springsecurityfinalmvn.models.SavingsModel;

public interface SavingRepo extends JpaRepository<SavingsModel,Long >  {
	 List<SavingsModel> findByOwnerId(String ownerId);

}
