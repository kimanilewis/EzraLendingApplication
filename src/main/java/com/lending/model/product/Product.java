package com.lending.model.product;

import com.lending.model.TenureType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

  private String name;
  private TenureType tenureType;
  private Integer tenure;
  private BigDecimal serviceFee;
  private Boolean serviceFeeIsPercentage;
  private BigDecimal dailyFee;
  private BigDecimal lateFee;
  private Integer daysAfterDueForFee;
}
