package com.lending.controller;

import com.lending.dto.LoanRequest;
import com.lending.dto.LoanResponse;
import com.lending.dto.RepaymentRequest;
import com.lending.service.LoanService;
import jakarta.validation.Valid;
import java.util.List;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/loans")
@Validated
public class LoanController {

  @Autowired private LoanService loanService;

  @PostMapping
  public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest request)
      throws BadRequestException {
    LoanResponse loan = loanService.createLoan(request);
    return new ResponseEntity<>(loan, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<LoanResponse> getLoan(@PathVariable Long id) {
    LoanResponse loan = loanService.getLoanById(id);
    return ResponseEntity.ok(loan);
  }

  @GetMapping("/customer/{id}")
  public ResponseEntity<List<LoanResponse>> getLoanByCustomerId(@PathVariable Long id) {
    List<LoanResponse> loan = loanService.getLoansByCustomerId(id);
    return ResponseEntity.ok(loan);
  }

  @PostMapping("/{loanId}/repayments")
  public ResponseEntity<LoanResponse> processRepayment(
      @PathVariable Long loanId, @Valid @RequestBody RepaymentRequest request) {
    LoanResponse updatedLoan = loanService.processRepayment(loanId, request);
    return ResponseEntity.ok(updatedLoan);
  }
}
