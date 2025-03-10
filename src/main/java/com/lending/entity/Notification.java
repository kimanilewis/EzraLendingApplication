package com.lending.entity;

import com.lending.model.NotificationChannel;
import com.lending.model.NotificationEventType;
import com.lending.model.NotificationStatus;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "loan_id")
  private Loan loan;

  @ManyToOne
  @JoinColumn(name = "template_id")
  private NotificationTemplate template;

  @Enumerated(EnumType.STRING)
  @Column(name = "event_type", nullable = false)
  private NotificationEventType eventType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationChannel channel;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationStatus status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    if (status == null) {
      status = NotificationStatus.PENDING;
    }
  }
}
