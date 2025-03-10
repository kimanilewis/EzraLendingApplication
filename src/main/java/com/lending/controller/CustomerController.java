package com.lending.controller;

import com.lending.dto.CustomerRequest;
import com.lending.dto.LoanLimitRequest;
import com.lending.entity.Customer;
import com.lending.entity.LoanLimit;
import com.lending.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@Validated
public class CustomerController {

  @Autowired
  private CustomerService customerService;

  @PostMapping
  public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRequest request) {
    Customer customer = customerService.createCustomer(request);
    return new ResponseEntity<>(customer, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
    Customer customer = customerService.getCustomerById(id);
    return ResponseEntity.ok(customer);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
    Customer customer = customerService.updateCustomer(id, request);
    return ResponseEntity.ok(customer);
  }

  @PostMapping("/{customerId}/loan-limits")
  public ResponseEntity<LoanLimit> setLoanLimit(@PathVariable Long customerId, @Valid @RequestBody LoanLimitRequest request) {
    LoanLimit loanLimit = customerService.setLoanLimit(customerId, request);
    return new ResponseEntity<>(loanLimit, HttpStatus.CREATED);
  }
}
