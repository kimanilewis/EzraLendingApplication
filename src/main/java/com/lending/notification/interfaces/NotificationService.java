package com.lending.notification.interfaces;

import com.lending.model.NotificationEventType;
import java.util.Map;

public interface NotificationService {
  /**
   * Send a notification based on an event.
   *
   * @param eventType the type of event (LOAN_CREATED, DUE_REMINDER, etc.)
   * @param customerId the ID of the customer to notify
   * @param loanId the ID of the related loan (optional; can be null)
   * @param variables variables for substituting in the template content and subject
   */
  void sendNotification(
      NotificationEventType eventType, Long customerId, Long loanId, Map<String, String> variables);
}
