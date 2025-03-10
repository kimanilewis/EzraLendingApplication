package com.lending.repository;

import com.lending.entity.Customer;
import com.lending.entity.Loan;
import com.lending.entity.Notification;
import com.lending.model.NotificationEventType;
import com.lending.model.NotificationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  List<Notification> findByStatus(NotificationStatus status);

  List<Notification> findByCustomer(Customer customer);

  List<Notification> findByLoan(Loan loan);

  List<Notification> findByEventType(NotificationEventType eventType);
}
