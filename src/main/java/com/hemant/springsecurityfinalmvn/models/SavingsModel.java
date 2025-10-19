package com.hemant.springsecurityfinalmvn.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "savings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double savedAmount;

    private String icon;

    private String fileUrl;

    private LocalDate startDate;

    private LocalDate targetDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel owner;
}
