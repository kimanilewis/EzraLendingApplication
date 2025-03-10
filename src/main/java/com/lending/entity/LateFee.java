package com.lending.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "late_fee")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LateFee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(name = "fee_amount", precision = 10, scale = 2)
  private BigDecimal feeAmount;

  @Column(name = "fee_percentage", precision = 5, scale = 2)
  private BigDecimal feePercentage;

  @Column(name = "is_percentage", nullable = false)
  private Boolean isPercentage;

  @Column(name = "trigger_days_after_due", nullable = false)
  private Integer triggerDaysAfterDue;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
  }
}
