package com.hemant.springsecurityfinalmvn.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "income")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate date;

    private String description;

    private String fileUrl;

    private String icon;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel owner;
}
