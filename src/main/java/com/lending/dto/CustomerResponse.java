package com.lending.dto;

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
public class CustomerResponse {
  private Long id;
  private String name;
  private String email;
  private String phone;
  private Double loanLimit;
  private List<LoanResponse> loans;
  private List<LoanLimitResponse> loanLimits;
  //  private List<RepaymentResponse> repayments;
  //  private List<TransactionResponse> transactions;
  private boolean active;
}
