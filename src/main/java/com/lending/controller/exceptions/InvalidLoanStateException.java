package com.lending.controller.exceptions;

public class InvalidLoanStateException extends RuntimeException {
  public InvalidLoanStateException(String message) {
    super(message);
  }
}
