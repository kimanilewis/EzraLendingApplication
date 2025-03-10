package com.lending.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanLimitRequest {

  @NotNull(message = "Customer ID is required")
  private Long customerId;

  @NotNull(message = "Product ID is required")
  private Long productId;

  @NotNull(message = "Maximum amount is required")
  @Positive(message = "Maximum amount must be positive")
  private BigDecimal maxAmount;
}
