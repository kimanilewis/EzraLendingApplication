package com.lending.entity;

import com.lending.model.InstallmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "installment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Installment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "loan_id", nullable = false)
  private Loan loan;

  @Column(name = "installment_number", nullable = false)
  private Integer installmentNumber;

  @Column(name = "principal_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal principalAmount;

  @Column(name = "interest_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal interestAmount;

  @Column(name = "fee_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal feeAmount;

  @Column(name = "due_date", nullable = false)
  private LocalDate dueDate;

  @Column(name = "payment_date")
  private LocalDate paymentDate;

  @Column(name = "paid_amount", precision = 10, scale = 2)
  private BigDecimal paidAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private InstallmentStatus status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (feeAmount == null) {
      feeAmount = BigDecimal.ZERO;
    }
    if (paidAmount == null) {
      paidAmount = BigDecimal.ZERO;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
