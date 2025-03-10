package com.lending.dto;

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
public class RepaymentRequest {

  @NotNull(message = "Loan ID is required")
  private Long loanId;

  @NotNull(message = "Payment amount is required")
  @Positive(message = "Payment amount must be positive")
  private BigDecimal paymentAmount;

  @NotNull(message = "Payment date is required")
  private LocalDate paymentDate;

  private Long installmentId; // optional
}
