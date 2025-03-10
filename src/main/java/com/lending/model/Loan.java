package com.lending.model;

import com.lending.model.customer.Customer;
import com.lending.model.product.Product;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Loan {
  private Product product;
  private Customer customer;
  private BigDecimal amount;
  private LoanStructureType structureType;
  private LocalDate originationDate;
  private LocalDate dueDate;
  private LoanState loanState;
  private Integer numberOfInstallments;
}
