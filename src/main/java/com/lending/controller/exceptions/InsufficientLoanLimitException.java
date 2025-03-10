package com.lending.controller.exceptions;

public class InsufficientLoanLimitException extends RuntimeException {
  public InsufficientLoanLimitException(String message) {
    super(message);
  }
}
