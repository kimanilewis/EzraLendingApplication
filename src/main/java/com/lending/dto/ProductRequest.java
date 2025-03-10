package com.lending.dto;

import com.lending.model.TenureType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

  @NotBlank(message = "Product name is required")
  @Size(max = 100, message = "Product name cannot exceed 100 characters")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  private String description;

  @NotNull(message = "Tenure is required")
  @Min(value = 1, message = "Tenure must be at least 1")
  private Integer tenure;

  @NotNull(message = "Tenure type is required")
  private TenureType tenureType;

  @NotNull(message = "Interest rate is required")
  private BigDecimal interestRate;

  @NotNull(message = "Installment loan flag is required")
  private Boolean isInstallmentLoan;

  @NotNull(message = "Days after due for fee is required")
  @Min(value = 0, message = "Days after due for fee must be non-negative")
  private Integer daysAfterDueForFee;

  @Valid private List<ServiceFeeRequest> serviceFees;

  @Valid private List<DailyFeeRequest> dailyFees;

  @Valid private List<LateFeeRequest> lateFees;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ServiceFeeRequest {
    @NotBlank(message = "Fee name is required")
    @Size(max = 100, message = "Fee name cannot exceed 100 characters")
    private String name;

    private BigDecimal feeAmount;
    private BigDecimal feePercentage;

    @NotNull(message = "Is percentage flag is required")
    private Boolean isPercentage;

    @NotNull(message = "Apply at origination flag is required")
    private Boolean applyAtOrigination;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DailyFeeRequest {
    @NotBlank(message = "Fee name is required")
    @Size(max = 100, message = "Fee name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Fee amount is required")
    private BigDecimal feeAmount;

    @NotNull(message = "Apply when overdue flag is required")
    private Boolean applyWhenOverdue;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class LateFeeRequest {
    @NotBlank(message = "Fee name is required")
    @Size(max = 100, message = "Fee name cannot exceed 100 characters")
    private String name;

    private BigDecimal feeAmount;
    private BigDecimal feePercentage;

    @NotNull(message = "Is percentage flag is required")
    private Boolean isPercentage;

    @NotNull(message = "Trigger days after due is required")
    @Min(value = 1, message = "Trigger days after due must be at least 1")
    private Integer triggerDaysAfterDue;
  }
}
