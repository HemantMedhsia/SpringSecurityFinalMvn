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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;
 
    @Column(nullable = false)
    private String source;
    
    @Column(name="saved_amount",nullable = false)
    private Double savedAmount;

    private String icon;

    private String fileUrl;

    private LocalDate date;

    

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel owner;
}
