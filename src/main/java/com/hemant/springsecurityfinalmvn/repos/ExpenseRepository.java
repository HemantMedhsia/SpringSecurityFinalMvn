package com.hemant.springsecurityfinalmvn.repos;

import com.hemant.springsecurityfinalmvn.models.ExpenseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseModel, Long> {
    List<ExpenseModel> findByOwnerId(String ownerId);

    @Query("SELECT e FROM ExpenseModel e WHERE e.owner.id = :ownerId AND MONTH(e.date) = MONTH(CURRENT_DATE) AND YEAR(e.date) = YEAR(CURRENT_DATE)")
    List<ExpenseModel> findCurrentMonthExpenses(String ownerId);

    @Query("SELECT SUM(e.amount) FROM ExpenseModel e WHERE e.owner.id = :ownerId AND MONTH(e.date) = MONTH(CURRENT_DATE) AND YEAR(e.date) = YEAR(CURRENT_DATE)")
    Double findCurrentMonthTotal(String ownerId);

    @Query("SELECT e.category, SUM(e.amount) FROM ExpenseModel e WHERE e.owner.id = :ownerId GROUP BY e.category")
    List<Object[]> findCategoryTotals(String ownerId);

    @Query("""
            SELECT MONTH(e.date), SUM(e.amount)
            FROM ExpenseModel e
            WHERE e.owner.id = :ownerId AND e.date >= :start
            GROUP BY MONTH(e.date)
            ORDER BY MONTH(e.date)
            """)
    List<Object[]> findLast6Months(String ownerId, LocalDate start);


}
