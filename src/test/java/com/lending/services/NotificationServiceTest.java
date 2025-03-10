package com.lending.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lending.controller.exceptions.ResourceNotFoundException;
import com.lending.entity.Customer;
import com.lending.entity.Loan;
import com.lending.entity.NotificationTemplate;
import com.lending.model.NotificationChannel;
import com.lending.model.NotificationEventType;
import com.lending.notification.EmailNotificationSender;
import com.lending.repository.CustomerRepository;
import com.lending.repository.LoanRepository;
import com.lending.repository.NotificationRepository;
import com.lending.repository.NotificationTemplateRepository;
import com.lending.service.NotificationServiceImpl;
import java.util.Collections;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NotificationServiceTest {

  @Mock private NotificationTemplateRepository notificationTemplateRepository;

  @Mock private NotificationRepository notificationRepository;

  @Mock private CustomerRepository customerRepository;

  @Mock private LoanRepository loanRepository;

  @Mock private EmailNotificationSender emailNotificationSender;

  @InjectMocks private NotificationServiceImpl notificationService;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSendNotification_Success() {
    // Given a template
    NotificationTemplate template =
        NotificationTemplate.builder()
            .id(1L)
            .name("Test Template")
            .eventType(NotificationEventType.LOAN_CREATED)
            .channel(NotificationChannel.EMAIL)
            .subject("Loan Created")
            .templateContent("Hello {{firstName}}, your loan {{loanId}} was created.")
            .active(true)
            .build();
    when(notificationTemplateRepository.findByEventTypeAndActive(
            NotificationEventType.LOAN_CREATED, true))
        .thenReturn(Collections.singletonList(template));

    // Given a customer and loan
    Customer customer = Customer.builder().id(1L).firstName("John").build();
    when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(customer));
    Loan loan = Loan.builder().id(100L).build();
    when(loanRepository.findById(100L)).thenReturn(java.util.Optional.of(loan));

    // Persist notification
    when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    // When
    notificationService.sendNotification(
        NotificationEventType.LOAN_CREATED,
        1L,
        100L,
        new HashMap<String, String>() {
          {
            put("firstName", "John");
            put("loanId", "100");
          }
        });

  }

  @Test
  public void testSendNotification_TemplateNotFound() {
    // Given no templates found
    when(notificationTemplateRepository.findByEventTypeAndActive(
            NotificationEventType.LOAN_CREATED, true))
        .thenReturn(Collections.emptyList());

    try {
      notificationService.sendNotification(
          NotificationEventType.LOAN_CREATED, 1L, 100L, new HashMap<>());
    } catch (ResourceNotFoundException ex) {
      assertThat(ex.getMessage()).contains("No active notification templates found");
    }
  }
}
