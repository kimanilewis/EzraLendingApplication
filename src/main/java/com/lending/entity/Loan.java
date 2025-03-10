package com.lending.entity;

import com.lending.model.LoanState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id", nullable = false)
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "loan_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal loanAmount;

  @Column(name = "outstanding_principal", nullable = false, precision = 10, scale = 2)
  private BigDecimal outstandingPrincipal;

  @Column(name = "outstanding_interest", nullable = false, precision = 10, scale = 2)
  private BigDecimal outstandingInterest;

  @Column(name = "outstanding_fees", nullable = false, precision = 10, scale = 2)
  private BigDecimal outstandingFees;

  @Enumerated(EnumType.STRING)
  @Column(name = "loan_state", nullable = false)
  private LoanState loanState;

  @Column(name = "origination_date", nullable = false)
  private LocalDate originationDate;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "number_of_installments", nullable = false)
  private Integer numberOfInstallments;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Installment> installments;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (numberOfInstallments == null) {
      numberOfInstallments = 1;
    }
    if (outstandingInterest == null) {
      outstandingInterest = BigDecimal.ZERO;
    }
    if (outstandingFees == null) {
      outstandingFees = BigDecimal.ZERO;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
