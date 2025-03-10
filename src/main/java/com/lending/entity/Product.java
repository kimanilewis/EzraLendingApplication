package com.lending.entity;

import com.lending.model.TenureType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
  @Id
  @GeneratedValue(generator = "product_id_seq")
  @SequenceGenerator(name = "product_id_seq", sequenceName = "product_id_seq", allocationSize = 1)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(length = 500)
  private String description;

  @Column(nullable = false)
  private Integer tenure;

  @Enumerated(EnumType.STRING)
  @Column(name = "tenure_type", nullable = false)
  private TenureType tenureType;

  @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
  private BigDecimal interestRate;

  @Column(name = "is_installment_loan", nullable = false)
  private Boolean isInstallmentLoan;

  @Column(name = "days_after_due_for_fee", nullable = false)
  private Integer daysAfterDueForFee;

  @Column(name = "payment_due_date")
  private LocalDate paymentDueDate;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private Boolean active;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ServiceFee> serviceFees;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<DailyFee> dailyFees;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<LateFee> lateFees;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
    if (active == null) {
      active = true;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
