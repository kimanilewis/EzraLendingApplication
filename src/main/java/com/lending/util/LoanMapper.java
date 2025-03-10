package com.lending.util;

import com.lending.dto.LoanResponse;
import com.lending.entity.Installment;
import com.lending.entity.Loan;
import java.math.BigDecimal;
import java.util.List;

public class LoanMapper {
  public static LoanResponse mapToLoanResponse(Loan loan) {
    return LoanResponse.builder()
        .id(loan.getId())
        .customerId(loan.getCustomer().getId())
        .productId(loan.getProduct().getId())
        .loanAmount(loan.getLoanAmount())
        .totalRepaymentAmount(calculateTotalRepayment(loan))
        .outstandingFees(loan.getOutstandingFees())
        .loanState(loan.getLoanState())
        .originationDate(loan.getOriginationDate())
        .dueDate(loan.getDueDate())
        .installments(mapInstallments(loan.getInstallments()))
        .build();
  }

  private static List<Installment> mapInstallments(List<Installment> installments) {
    if (installments == null) return List.of();

    return installments.stream()
        .map(
            i ->
                Installment.builder()
                    .installmentNumber(i.getInstallmentNumber())
                    .interestAmount(i.getInterestAmount())
                    .paidAmount(i.getPaidAmount())
                    .status(i.getStatus())
                    .feeAmount(i.getFeeAmount())
                    .build())
        .toList();
  }

  private static BigDecimal calculateTotalRepayment(Loan loan) {
    return loan.getLoanAmount().add(loan.getOutstandingInterest()).add(loan.getOutstandingFees());
  }
}
