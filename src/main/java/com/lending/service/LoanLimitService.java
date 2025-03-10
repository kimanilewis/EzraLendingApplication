package com.lending.service;

import com.lending.controller.exceptions.ResourceNotFoundException;
import com.lending.dto.LoanLimitRequest;
import com.lending.dto.LoanLimitResponse;
import com.lending.entity.Customer;
import com.lending.entity.LoanLimit;
import com.lending.entity.Product;
import com.lending.repository.CustomerRepository;
import com.lending.repository.LoanLimitRepository;
import com.lending.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanLimitService {

  @Autowired private LoanLimitRepository loanLimitRepository;

  @Autowired private CustomerRepository customerRepository;

  @Autowired private ProductRepository productRepository;

  public List<LoanLimitResponse> getAllLoanLimits() {
    return loanLimitRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<LoanLimitResponse> getLoanLimitsByCustomerId(Long customerId) {
    if (!customerRepository.existsById(customerId)) {
      throw new ResourceNotFoundException("Customer not found with id: " + customerId);
    }
    return loanLimitRepository.findByCustomerId(customerId).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public LoanLimitResponse getLoanLimitById(Long id) {
    LoanLimit loanLimit =
        loanLimitRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Loan limit not found with id: " + id));
    return convertToDTO(loanLimit);
  }

  public LoanLimitResponse getLoanLimitByCustomerAndProduct(Long customerId, Long productId) {
    LoanLimit loanLimit =
        loanLimitRepository
            .findByCustomerIdAndProductId(customerId, productId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Loan limit not found for customer id: "
                            + customerId
                            + " and product id: "
                            + productId));
    return convertToDTO(loanLimit);
  }

  @Transactional
  public LoanLimitResponse createLoanLimit(LoanLimitRequest loanLimitRequest) {
    Customer customer =
        customerRepository
            .findById(loanLimitRequest.getCustomerId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Customer not found with id: " + loanLimitRequest.getCustomerId()));

    Product product =
        productRepository
            .findById(loanLimitRequest.getProductId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Product not found with id: " + loanLimitRequest.getProductId()));

    // Check if a loan limit already exists for this customer and product
    loanLimitRepository
        .findByCustomerIdAndProductId(
            loanLimitRequest.getCustomerId(), loanLimitRequest.getProductId())
        .ifPresent(
            existingLimit -> {
              throw new IllegalStateException(
                  "Loan limit already exists for this customer and product");
            });

    LoanLimit loanLimit = new LoanLimit();
    loanLimit.setCustomer(customer);
    loanLimit.setProduct(product);
    loanLimit.setMaxAmount(loanLimitRequest.getMaxAmount());
    loanLimit.setCurrentUtilized(loanLimitRequest.getMaxAmount());
    loanLimit.setCreatedAt(LocalDateTime.now());
    loanLimit.setUpdatedAt(LocalDateTime.now());

    LoanLimit savedLoanLimit = loanLimitRepository.save(loanLimit);
    return convertToDTO(savedLoanLimit);
  }

  @Transactional
  public LoanLimitResponse updateLoanLimit(Long id, LoanLimitRequest loanLimitRequest) {
    LoanLimit existingLoanLimit =
        loanLimitRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Loan limit not found with id: " + id));

    // If customer or product is changing, verify they exist
    if (!existingLoanLimit.getCustomer().getId().equals(loanLimitRequest.getCustomerId())) {
      Customer customer =
          customerRepository
              .findById(loanLimitRequest.getCustomerId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Customer not found with id: " + loanLimitRequest.getCustomerId()));
      existingLoanLimit.setCustomer(customer);
    }

    if (!existingLoanLimit.getProduct().getId().equals(loanLimitRequest.getProductId())) {
      Product product =
          productRepository
              .findById(loanLimitRequest.getProductId())
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Product not found with id: " + loanLimitRequest.getProductId()));
      existingLoanLimit.setProduct(product);
    }

    existingLoanLimit.setMaxAmount(loanLimitRequest.getMaxAmount());
    existingLoanLimit.setCurrentUtilized(loanLimitRequest.getMaxAmount()); // TODO:
    existingLoanLimit.setUpdatedAt(LocalDateTime.now());

    LoanLimit updatedLoanLimit = loanLimitRepository.save(existingLoanLimit);
    return convertToDTO(updatedLoanLimit);
  }

  @Transactional
  public void deleteLoanLimit(Long id) {
    if (!loanLimitRepository.existsById(id)) {
      throw new ResourceNotFoundException("Loan limit not found with id: " + id);
    }
    loanLimitRepository.deleteById(id);
  }

  private LoanLimitResponse convertToDTO(LoanLimit loanLimit) {
    LoanLimitResponse dto = new LoanLimitResponse();
    dto.setId(loanLimit.getId());
    dto.setCustomerId(loanLimit.getCustomer().getId());
    dto.setProductId(loanLimit.getProduct().getId());
    dto.setMaxAmount(loanLimit.getMaxAmount());
    dto.setCurrentUtilized(loanLimit.getCurrentUtilized());
    return dto;
  }
}
