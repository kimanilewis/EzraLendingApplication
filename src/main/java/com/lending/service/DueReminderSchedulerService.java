package com.lending.service;

import com.lending.entity.Loan;
import com.lending.model.LoanState;
import com.lending.model.NotificationEventType;
import com.lending.notification.interfaces.NotificationService;
import com.lending.repository.LoanRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DueReminderSchedulerService {

  private final LoanRepository loanRepository;

  private final NotificationService notificationService;

  /**
   * Scheduled job to send due date reminders for loans that are due tomorrow. Runs daily at 8 AM.
   */
  @Scheduled(cron = "0 0 8 * * ?")
  public void sendDueDateReminders() {
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    List<Loan> loansDueTomorrow =
        loanRepository.findByDueDateAndLoanState(tomorrow, LoanState.OPEN);
    log.info("Found {} loans due on {}", loansDueTomorrow.size(), tomorrow);
    for (Loan loan : loansDueTomorrow) {
      try {
        Map<String, String> variables = new HashMap<>();
        variables.put("firstName", loan.getCustomer().getFirstName());
        variables.put("loanId", loan.getId().toString());
        variables.put("dueDate", loan.getDueDate().toString());
        notificationService.sendNotification(
            NotificationEventType.PAYMENT_DUE, loan.getCustomer().getId(), loan.getId(), variables);
        log.info("Due reminder sent for loan id {}", loan.getId());
      } catch (Exception ex) {
        log.error("Error sending due reminder for loan id {}: {}", loan.getId(), ex.getMessage());
      }
    }
  }
}
