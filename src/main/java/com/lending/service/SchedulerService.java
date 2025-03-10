package com.lending.service;

import com.lending.entity.Loan;
import com.lending.model.LoanState;
import com.lending.model.NotificationEventType;
import com.lending.notification.interfaces.NotificationService;
import com.lending.repository.LoanRepository;
import java.math.BigDecimal;
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
public class SchedulerService {

  private final LoanRepository loanRepository;

  private final NotificationService notificationService;

  /**
   * Scheduled job that runs daily at midnight to process overdue loans. It marks overdue loans,
   * applies a fixed late fee, saves the updated loan, and sends a notification to the customer.
   */
  @Scheduled(cron = "0 0 0 * * ?")
  public void processOverdueLoans() {
    log.info("Starting overdue loans sweep job at {}", LocalDate.now());
    // Find all loans that are past due and still open
    List<Loan> overdueLoans =
        loanRepository.findByDueDateBeforeAndLoanState(LocalDate.now(), LoanState.OPEN);
    log.info("Found {} overdue loans", overdueLoans.size());
    for (Loan loan : overdueLoans) {
      try {
        // Mark loan as overdue
        loan.setLoanState(LoanState.OVERDUE);

        // Apply a late fee (for example, a fixed fee of 10.00)
        BigDecimal lateFee = new BigDecimal("10.00");
        if (loan.getOutstandingFees() == null) {
          loan.setOutstandingFees(lateFee);
        } else {
          loan.setOutstandingFees(loan.getOutstandingFees().add(lateFee));
        }
        loanRepository.save(loan);
        log.info("Loan id {} marked as OVERDUE and late fee applied", loan.getId());

        // Prepare notification variables
        Map<String, String> variables = new HashMap<>();
        variables.put("firstName", loan.getCustomer().getFirstName());
        variables.put("lateFee", lateFee.toString());
        variables.put("loanId", loan.getId().toString());
        variables.put("dueDate", loan.getDueDate().toString());

        // Send notification to customer for overdue loan event
        notificationService.sendNotification(
            NotificationEventType.PAYMENT_OVERDUE,
            loan.getCustomer().getId(),
            loan.getId(),
            variables);
        log.info("Overdue notification sent for loan id {}", loan.getId());
      } catch (Exception ex) {
        log.error("Error processing overdue loan id {}: {}", loan.getId(), ex.getMessage(), ex);
      }
    }
    log.info("Overdue loans sweep job completed");
  }
}
