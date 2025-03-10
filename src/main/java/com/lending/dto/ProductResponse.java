package com.lending.dto;

import com.lending.entity.DailyFee;
import com.lending.entity.LateFee;
import com.lending.entity.ServiceFee;
import com.lending.model.TenureType;
import java.math.BigDecimal;
import java.util.List;
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
public class ProductResponse {

  private Long id;
  private String name;
  private String description;
  private Integer tenure;
  private TenureType tenureType;
  private BigDecimal interestRate;
  private List<ServiceFee> serviceFees;
  private Boolean serviceFeeIsPercentage;
  private List<DailyFee> dailyFees;
  private List<LateFee> lateFees;
  private Integer daysAfterDueForFee;
}
