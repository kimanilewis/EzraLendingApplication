package com.lending.service;

import com.lending.controller.exceptions.*;
import com.lending.dto.LoanRequest;
import com.lending.dto.LoanResponse;
import com.lending.dto.RepaymentRequest;
import com.lending.entity.*;
import com.lending.entity.Loan;
import com.lending.model.*;
import com.lending.notification.interfaces.NotificationService;
import com.lending.repository.*;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanService {

  private final LoanRepository loanRepository;
  private final LoanLimitRepository loanLimitRepository;
  private final ProductRepository productRepository;
  private final CustomerRepository customerRepository;
  private final InstallmentRepository installmentRepository;
  private final NotificationService notificationService;

  @Transactional
  public LoanResponse createLoan(LoanRequest request) {
    Customer customer = validateCustomerExists(request.getCustomerId());
    Product product = validateProductExistsAndActive(request.getProductId());
    validateCustomerActive(customer);

    LoanLimit loanLimit = validateLoanLimitExists(customer, product);
    validateLoanAmount(request.getLoanAmount(), loanLimit);

    Loan loan = buildLoan(request, customer, product);
    updateLoanLimit(loanLimit, request.getLoanAmount());

    Loan savedLoan = saveLoanWithInstallments(loan, product);
    sendLoanCreatedNotification(customer, savedLoan);

    return mapToLoanResponse(savedLoan);
  }

  public LoanResponse getLoanById(Long id) {
    return loanRepository
        .findById(id)
        .map(this::mapToLoanResponse)
        .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + id));
  }

  public List<LoanResponse> getLoansByCustomerId(Long customerId) {
    validateCustomerExists(customerId);
    return loanRepository.findByCustomerId(customerId).stream()
        .map(this::mapToLoanResponse)
        .toList();
  }

  @Transactional
  public LoanResponse processRepayment(Long loanId, RepaymentRequest request) {
    validateRepaymentRequest(request);
    Loan loan = validateLoanExists(loanId);
    validateLoanStateForRepayment(loan);

    BigDecimal remainingPayment = applyPaymentToLoan(loan, request.getPaymentAmount());
    handleInstallmentPayment(request, remainingPayment);

    updateLoanStateAndLimit(loan);
    sendPaymentNotification(loan, request);

    return mapToLoanResponse(loanRepository.save(loan));
  }

  // Region: Validation Methods
  private Customer validateCustomerExists(Long customerId) {
    return customerRepository
        .findById(customerId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Customer not found with id " + customerId));
  }

  private Product validateProductExistsAndActive(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id " + productId));

    if (!product.getActive()) {
      throw new IllegalStateException("Product with ID " + productId + " is not active");
    }
    return product;
  }

  private void validateCustomerActive(Customer customer) {
    if (!customer.getActive()) {
      throw new IllegalStateException("Customer with ID " + customer.getId() + " is not active");
    }
  }

  private LoanLimit validateLoanLimitExists(Customer customer, Product product) {
    return loanLimitRepository
        .findByCustomerAndProduct(customer, product)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "No loan limit found for customer ID "
                        + customer.getId()
                        + " and product ID "
                        + product.getId()));
  }

  private void validateLoanAmount(BigDecimal requestedAmount, LoanLimit loanLimit) {
    BigDecimal availableLimit = loanLimit.getMaxAmount().subtract(loanLimit.getCurrentUtilized());

    if (requestedAmount.compareTo(availableLimit) > 0) {
      throw new InsufficientLoanLimitException(
          "Requested amount: " + requestedAmount + " exceeds available limit: " + availableLimit);
    }
  }

  private Loan validateLoanExists(Long loanId) {
    return loanRepository
        .findById(loanId)
        .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + loanId));
  }

  private void validateLoanStateForRepayment(Loan loan) {
    if (loan.getLoanState() == LoanState.CLOSED) {
      throw new InvalidLoanStateException("Loan ID " + loan.getId() + " is already closed");
    }
    if (loan.getLoanState() == LoanState.CANCELLED) {
      throw new InvalidLoanStateException(
          "Loan ID " + loan.getId() + " is cancelled and cannot accept payments");
    }
  }

  private void validateRepaymentRequest(RepaymentRequest request) {
    if (request.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Payment amount must be greater than zero");
    }

    if (request.getInstallmentId() != null && request.getLoanId() == null) {
      throw new IllegalArgumentException("Loan ID must be provided with installment ID");
    }
  }

  // End Region

  // Region: Core Business Logic
  private Loan buildLoan(LoanRequest request, Customer customer, Product product) {
    return Loan.builder()
        .customer(customer)
        .product(product)
        .loanAmount(request.getLoanAmount())
        .outstandingPrincipal(request.getLoanAmount())
        .loanState(LoanState.OPEN)
        .originationDate(validateOriginationDate(request.getOriginationDate()))
        .dueDate(
            calculateDueDate(
                request.getOriginationDate(), product.getTenure(), product.getTenureType()))
        .numberOfInstallments(validateInstallmentCount(request.getNumberOfInstallments(), product))
        .build();
  }

  private Loan saveLoanWithInstallments(Loan loan, Product product) {
    Loan savedLoan = loanRepository.save(loan);

    if (shouldGenerateInstallments(product, savedLoan)) {
      List<Installment> installments = generateInstallments(savedLoan);
      savedLoan.setInstallments(installments);
      return loanRepository.save(savedLoan);
    }

    return savedLoan;
  }

  private void updateLoanLimit(LoanLimit loanLimit, BigDecimal loanAmount) {
    BigDecimal newUtilized = loanLimit.getCurrentUtilized().add(loanAmount);

    if (newUtilized.compareTo(loanLimit.getMaxAmount()) > 0) {
      throw new InsufficientLoanLimitException(
          "Loan would exceed total limit. Current: "
              + loanLimit.getCurrentUtilized()
              + ", Max: "
              + loanLimit.getMaxAmount());
    }

    loanLimit.setCurrentUtilized(newUtilized);
    loanLimit.setUpdatedAt(LocalDateTime.now());
    loanLimitRepository.save(loanLimit);
  }

  private BigDecimal applyPaymentToLoan(Loan loan, BigDecimal payment) {
    PaymentAllocation allocation = new PaymentAllocation(payment);
    allocation
        .applyTo(loan.getOutstandingFees(), loan::setOutstandingFees)
        .applyTo(loan.getOutstandingInterest(), loan::setOutstandingInterest)
        .applyTo(loan.getOutstandingPrincipal(), loan::setOutstandingPrincipal);
    return allocation.getRemainingPayment();
  }

  private void handleInstallmentPayment(RepaymentRequest request, BigDecimal remainingPayment) {
    if (request.getInstallmentId() != null) {
      Installment installment =
          installmentRepository
              .findById(request.getInstallmentId())
              .orElseThrow(() -> new ResourceNotFoundException("Installment not found"));
      updateInstallment(installment, remainingPayment);
    }
  }

  private void updateInstallment(Installment installment, BigDecimal payment) {
    installment.setPaidAmount(installment.getPaidAmount().add(payment));
    if (isInstallmentPaid(installment)) {
      installment.setStatus(InstallmentStatus.PAID);
      installment.setPaymentDate(LocalDate.now());
    }
    installmentRepository.save(installment);
  }

  private void updateLoanStateAndLimit(Loan loan) {
    if (isLoanFullyPaid(loan)) {
      loan.setLoanState(LoanState.CLOSED);
      releaseLoanLimit(loan);
    } else if (shouldReopenLoan(loan)) {
      loan.setLoanState(LoanState.OPEN);
    }
  }

  // End Region

  // Region: Helper Methods
  private LocalDate validateOriginationDate(LocalDate originationDate) {
    if (originationDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Origination date cannot be in the future");
    }
    return originationDate;
  }

  private int validateInstallmentCount(Integer requestedInstallments, Product product) {
    if (!product.getIsInstallmentLoan() && requestedInstallments != null) {
      throw new IllegalArgumentException("Installments not allowed for non-installment products");
    }

    int defaultInstallments = 1;
    int maxInstallments = 12; // TO DO make it configurable or add to product

    if (requestedInstallments == null) {
      return product.getIsInstallmentLoan() ? defaultInstallments : 0;
    }

    if (requestedInstallments < 1) {
      throw new IllegalArgumentException("Installment count must be at least 1");
    }

    if (maxInstallments > 0 && requestedInstallments > maxInstallments) {
      throw new IllegalArgumentException(
          "Installment count exceeds maximum allowed: " + maxInstallments);
    }

    return requestedInstallments;
  }

  private boolean shouldGenerateInstallments(Product product, Loan loan) {
    return Boolean.TRUE.equals(product.getIsInstallmentLoan())
        && loan.getNumberOfInstallments() > 1;
  }

  private boolean isInstallmentPaid(Installment installment) {
    return installment
            .getPaidAmount()
            .compareTo(
                installment
                    .getPrincipalAmount()
                    .add(installment.getInterestAmount())
                    .add(installment.getFeeAmount()))
        >= 0;
  }

  private boolean isLoanFullyPaid(Loan loan) {
    return loan.getOutstandingPrincipal().signum() == 0
        && loan.getOutstandingInterest().signum() == 0
        && loan.getOutstandingFees().signum() == 0;
  }

  private boolean shouldReopenLoan(Loan loan) {
    return loan.getLoanState() == LoanState.OVERDUE
        && LocalDate.now()
            .isBefore(loan.getDueDate().plusDays(loan.getProduct().getDaysAfterDueForFee()));
  }

  private void releaseLoanLimit(Loan loan) {
    LoanLimit limit =
        loanLimitRepository
            .findByCustomerAndProduct(loan.getCustomer(), loan.getProduct())
            .orElseThrow(() -> new ResourceNotFoundException("Loan limit not found"));
    limit.setCurrentUtilized(limit.getCurrentUtilized().subtract(loan.getLoanAmount()));
    limit.setCurrentUtilized(limit.getCurrentUtilized().max(BigDecimal.ZERO));
    limit.setUpdatedAt(LocalDateTime.now());
    loanLimitRepository.save(limit);
  }

  private LocalDate calculateDueDate(LocalDate originationDate, int tenure, TenureType tenureType) {
    return switch (tenureType) {
      case DAYS -> originationDate.plusDays(tenure);
      case MONTHS -> originationDate.plusMonths(tenure);
      default -> originationDate.plusDays(tenure);
    };
  }

  private List<Installment> generateInstallments(Loan loan) {
    int numInstallments = loan.getNumberOfInstallments();
    BigDecimal installmentPrincipal =
        loan.getLoanAmount().divide(BigDecimal.valueOf(numInstallments), 2, RoundingMode.HALF_UP);

    List<Installment> installments = new ArrayList<>();
    for (int i = 1; i <= numInstallments; i++) {
      installments.add(createInstallment(loan, i, installmentPrincipal));
    }
    return installments;
  }

  private Installment createInstallment(Loan loan, int index, BigDecimal principal) {
    return Installment.builder()
        .loan(loan)
        .installmentNumber(index)
        .principalAmount(principal)
        .interestAmount(BigDecimal.ZERO)
        .feeAmount(BigDecimal.ZERO)
        .dueDate(calculateInstallmentDueDate(loan, index))
        .status(InstallmentStatus.PENDING)
        .build();
  }

  private LocalDate calculateInstallmentDueDate(Loan loan, int index) {
    return loan.getOriginationDate()
        .plusMonths(index)
        .withDayOfMonth(loan.getProduct().getPaymentDueDate().getDayOfMonth());
  }

  // End Region

  // Region: Notification Methods
  private void sendLoanCreatedNotification(Customer customer, Loan loan) {
    Map<String, String> variables = createNotificationVariables(customer, loan);
    notificationService.sendNotification(
        NotificationEventType.LOAN_CREATED, customer.getId(), loan.getId(), variables);
  }

  private void sendPaymentNotification(Loan loan, RepaymentRequest request) {
    Map<String, String> variables = createNotificationVariables(loan.getCustomer(), loan);
    variables.put("paymentAmount", request.getPaymentAmount().toString());
    notificationService.sendNotification(
        NotificationEventType.PAYMENT_RECEIVED,
        loan.getCustomer().getId(),
        loan.getId(),
        variables);
  }

  private Map<String, String> createNotificationVariables(Customer customer, Loan loan) {
    Map<String, String> variables = new HashMap<>();
    variables.put("firstName", customer.getFirstName());
    variables.put("loanId", loan.getId().toString());
    variables.put("loanAmount", loan.getLoanAmount().toString());
    return variables;
  }

  // End Region

  // Region: DTO Mapping
  private LoanResponse mapToLoanResponse(Loan loan) {
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

  private BigDecimal calculateTotalRepayment(Loan loan) {
    return loan.getLoanAmount().add(loan.getOutstandingInterest()).add(loan.getOutstandingFees());
  }

  private List<Installment> mapInstallments(List<Installment> installments) {
    if (installments == null) return List.of();

    return installments.stream().map(this::mapInstallment).toList();
  }

  private Installment mapInstallment(Installment installment) {
    return Installment.builder()
        .installmentNumber(installment.getInstallmentNumber())
        .interestAmount(installment.getInterestAmount())
        .paidAmount(installment.getPaidAmount())
        .status(installment.getStatus())
        .feeAmount(installment.getFeeAmount())
        .build();
  }

  // End Region

  // Region: Payment Allocation
  private static class PaymentAllocation {
    private BigDecimal remainingPayment;

    PaymentAllocation(BigDecimal payment) {
      this.remainingPayment = payment;
    }

    PaymentAllocation applyTo(
        BigDecimal outstanding, java.util.function.Consumer<BigDecimal> setter) {
      if (remainingPayment.signum() > 0 && outstanding.signum() > 0) {
        BigDecimal payment = remainingPayment.min(outstanding);
        setter.accept(outstanding.subtract(payment));
        remainingPayment = remainingPayment.subtract(payment);
      }
      return this;
    }

    BigDecimal getRemainingPayment() {
      return remainingPayment;
    }
  }
  // End Region
}
