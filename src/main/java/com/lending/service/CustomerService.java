package com.lending.service;

import com.lending.controller.exceptions.ResourceNotFoundException;
import com.lending.dto.CustomerRequest;
import com.lending.dto.LoanLimitRequest;
import com.lending.entity.Customer;
import com.lending.entity.LoanLimit;
import com.lending.entity.Product;
import com.lending.repository.CustomerRepository;
import com.lending.repository.LoanLimitRepository;
import com.lending.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {

  private final CustomerRepository customerRepository;

  private final LoanLimitRepository loanLimitRepository;

  private final ProductRepository productRepository;

  @Transactional
  public Customer createCustomer(CustomerRequest request) {
    Customer customer =
        Customer.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .address(request.getAddress())
            .dateOfBirth(request.getDateOfBirth())
            .creditScore(request.getCreditScore())
            .monthlyIncome(request.getMonthlyIncome())
            .active(true)
            .build();
    return customerRepository.save(customer);
  }

  public Customer getCustomerById(Long id) {
    return customerRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
  }

  @Transactional
  public Customer updateCustomer(Long id, CustomerRequest request) {
    Customer customer = getCustomerById(id);
    customer.setFirstName(request.getFirstName());
    customer.setLastName(request.getLastName());
    customer.setEmail(request.getEmail());
    customer.setPhone(request.getPhone());
    customer.setAddress(request.getAddress());
    customer.setDateOfBirth(request.getDateOfBirth());
    customer.setCreditScore(request.getCreditScore());
    customer.setMonthlyIncome(request.getMonthlyIncome());
    return customerRepository.save(customer);
  }

  @Transactional
  public LoanLimit setLoanLimit(Long customerId, LoanLimitRequest request) {
    Customer customer = getCustomerById(customerId);
    Product product =
        productRepository
            .findById(request.getProductId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Product not found with id " + request.getProductId()));

    // Look up an existing loan limit or create a new one
    LoanLimit loanLimit =
        loanLimitRepository
            .findByCustomerAndProduct(customer, product)
            .orElse(
                LoanLimit.builder()
                    .customer(customer)
                    .product(product)
                    .currentUtilized(new java.math.BigDecimal("0.00"))
                    .build());
    loanLimit.setMaxAmount(request.getMaxAmount());
    return loanLimitRepository.save(loanLimit);
  }
}
