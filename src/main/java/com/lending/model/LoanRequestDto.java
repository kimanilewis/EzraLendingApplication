package com.lending.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {
  @NotNull(message = "User ID is required")
  private Long userId;

  @Positive(message = "Loan amount must be positive")
  private BigDecimal amount;

  @DecimalMin(value = "0.0", message = "Interest rate must be non-negative")
  private BigDecimal interestRate;

  @Future(message = "Due date must be in the future")
  private LocalDateTime dueDate;
}
