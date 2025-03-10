package com.lending.repository;

import com.lending.entity.Installment;
import com.lending.entity.Loan;
import com.lending.model.InstallmentStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentRepository extends JpaRepository<Installment, Long> {
  List<Installment> findByLoan(Loan loan);

  List<Installment> findByLoanAndStatus(Loan loan, InstallmentStatus status);

  @Query("SELECT i FROM Installment i WHERE i.status = 'PENDING' AND i.dueDate < :currentDate")
  List<Installment> findOverdueInstallments(LocalDate currentDate);
}
