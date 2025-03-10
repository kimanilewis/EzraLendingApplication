package com.lending.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

  @NotNull(message = "Customer ID is required")
  private Long customerId;

  @NotNull(message = "Product ID is required")
  private Long productId;

  @NotNull(message = "Loan amount is required")
  @Positive(message = "Loan amount must be positive")
  private BigDecimal loanAmount;

  @NotNull(message = "Origination date is required")
  private LocalDate originationDate;

  @Min(value = 1, message = "Number of installments must be at least 1")
  private Integer numberOfInstallments;
}
