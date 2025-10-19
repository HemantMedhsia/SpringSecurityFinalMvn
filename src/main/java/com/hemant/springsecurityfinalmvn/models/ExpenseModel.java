package com.hemant.springsecurityfinalmvn.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double amount;

    private String category;

    @Column(nullable = false)
    private LocalDate date;

    private String description;

    private String fileUrl;

    private String icon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel owner;
}
