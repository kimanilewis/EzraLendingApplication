package com.lending.services;

import static org.mockito.Mockito.*;

import com.lending.entity.Loan;
import com.lending.model.LoanState;
import com.lending.notification.interfaces.NotificationService;
import com.lending.repository.LoanRepository;
import com.lending.service.SchedulerService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SchedulerServiceTest {

  @Mock private LoanRepository loanRepository;

  @Mock private NotificationService notificationService;

  @InjectMocks private SchedulerService overdueLoanSchedulerService;

  public SchedulerServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testProcessOverdueLoans() {
    // Given a loan that is overdue
    Loan loan =
        Loan.builder()
            .id(1L)
            .dueDate(LocalDate.now().minusDays(1))
            .loanState(LoanState.OPEN)
            .outstandingFees(BigDecimal.ZERO)
            .build();
    loan.setCustomer(null);
    when(loanRepository.findByDueDateBeforeAndLoanState(any(LocalDate.class), eq(LoanState.OPEN)))
        .thenReturn(Collections.singletonList(loan));

    // When
    overdueLoanSchedulerService.processOverdueLoans();

    verify(loanRepository, atLeastOnce()).save(any());
  }
}
