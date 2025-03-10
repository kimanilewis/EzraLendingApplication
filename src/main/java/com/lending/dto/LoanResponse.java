package com.lending.dto;

import com.lending.entity.Installment;
import com.lending.model.LoanState;
import com.lending.model.customer.Customer;
import java.math.BigDecimal;
import java.time.LocalDate;
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
public class LoanResponse {

  private Long id;
  private String productName;
  private String customerName;
  private BigDecimal loanAmount;
  private Long customerId;
  private Long productId;
  private BigDecimal outstandingPrincipal;
  private BigDecimal outstandingInterest;
  private BigDecimal outstandingFees;
  private BigDecimal totalRepaymentAmount;
  private LoanState loanState;
  private LocalDate originationDate;
  private LocalDate dueDate;
  private Integer numberOfInstallments;
  private LocalDate createdAt;
  private LocalDate updatedAt;
  private Integer remainingInstallments;
  private List<Installment> installments;
}
