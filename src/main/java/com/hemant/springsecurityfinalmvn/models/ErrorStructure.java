package com.hemant.springsecurityfinalmvn.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorStructure {
    private String status;
    private String message;
    private String details;
    private LocalDateTime timestamp;
}
