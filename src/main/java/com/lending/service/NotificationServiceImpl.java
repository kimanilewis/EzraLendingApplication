package com.lending.service;

import com.lending.controller.exceptions.ResourceNotFoundException;
import com.lending.entity.Customer;
import com.lending.entity.Loan;
import com.lending.entity.Notification;
import com.lending.entity.NotificationTemplate;
import com.lending.model.NotificationChannel;
import com.lending.model.NotificationEventType;
import com.lending.model.NotificationStatus;
import com.lending.notification.EmailNotificationSender;
import com.lending.notification.interfaces.NotificationSender;
import com.lending.notification.interfaces.NotificationService;
import com.lending.notification.PushNotificationSender;
import com.lending.notification.SmsNotificationSender;
import com.lending.repository.CustomerRepository;
import com.lending.repository.LoanRepository;
import com.lending.repository.NotificationRepository;
import com.lending.repository.NotificationTemplateRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

  private final NotificationTemplateRepository notificationTemplateRepository;

  private final NotificationRepository notificationRepository;

  private final CustomerRepository customerRepository;

  private final LoanRepository loanRepository;

  private final EmailNotificationSender emailNotificationSender;

  private final SmsNotificationSender smsNotificationSender;

  private final PushNotificationSender pushNotificationSender;

  /**
   * Send a notification based on an event. This method looks up an active template for the given
   * event type and applies additional configurable rules (e.g., based on the loan product or
   * customer segment) to determine the correct template.
   */
  @Override
  @Transactional
  public void sendNotification(
      NotificationEventType eventType,
      Long customerId,
      Long loanId,
      Map<String, String> variables) {
    // Retrieve active templates for the event. For demo purposes, assume we take the first one.
    List<NotificationTemplate> templates =
        notificationTemplateRepository.findByEventTypeAndActive(eventType, true);
    if (templates.isEmpty()) {
      throw new ResourceNotFoundException(
          "No active notification templates found for event " + eventType);
    }

    // Optionally, apply additional rule filtering based on customer segment or product attributes
    NotificationTemplate template = templates.get(0);

    Customer customer =
        customerRepository
            .findById(customerId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Customer not found with id " + customerId));
    Loan loan = null;
    if (loanId != null) {
      loan =
          loanRepository
              .findById(loanId)
              .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id " + loanId));
    }

    // Substitute variables in template content and subject
    String content = substituteVariables(template.getTemplateContent(), variables);
    String subject =
        template.getSubject() != null ? substituteVariables(template.getSubject(), variables) : "";

    // Build notification record
    Notification notification =
        Notification.builder()
            .customer(customer)
            .loan(loan)
            .template(template)
            .eventType(template.getEventType())
            .channel(template.getChannel())
            .content(content)
            .status(NotificationStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();
    notification = notificationRepository.save(notification);

    // Delegate sending based on channel
    NotificationSender sender = getSenderForChannel(template.getChannel());
    if (sender != null) {
      sender.send(notification, subject);
      notification.setStatus(NotificationStatus.SENT);
      notification.setSentAt(LocalDateTime.now());
    } else {
      notification.setStatus(NotificationStatus.FAILED);
    }
    notificationRepository.save(notification);
  }

  /** Returns the appropriate sender implementation for the given notification channel. */
  private NotificationSender getSenderForChannel(NotificationChannel channel) {
    if (channel == null) return null;
    switch (channel) {
      case EMAIL:
        return emailNotificationSender;
      case SMS:
        return smsNotificationSender;
      case PUSH:
        return pushNotificationSender;
      default:
        return null;
    }
  }

  /**
   * Performs simple variable substitution by replacing placeholders like {{key}} with their values.
   */
  private String substituteVariables(String templateContent, Map<String, String> variables) {
    String content = templateContent;
    if (variables != null) {
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
      }
    }
    return content;
  }
}
