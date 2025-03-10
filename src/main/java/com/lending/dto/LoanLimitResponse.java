package com.lending.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanLimitResponse {
  private Long id;
  private Long customerId;
  private Long productId;
  private String customerName;
  private String productName;
  private BigDecimal maxAmount;
  private BigDecimal currentUtilized;
}
