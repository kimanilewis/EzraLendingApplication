package com.lending.repository;

import com.lending.entity.Customer;
import com.lending.entity.Loan;
import com.lending.entity.Product;
import com.lending.model.LoanState;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
  List<Loan> findByCustomerId(Long id);

  List<Loan> findByLoanState(LoanState loanState);

  List<Loan> findByCustomerAndLoanState(Customer customer, LoanState loanState);

  List<Loan> findByProduct(Product product);

  @Query("SELECT l FROM Loan l WHERE l.loanState = 'OPEN' AND l.dueDate < :currentDate")
  List<Loan> findOverdueLoans(LocalDate currentDate);

  BigDecimal findTotalOutstandingByCustomerId(Long id);

  List<Loan> findByDueDateBeforeAndLoanState(LocalDate now, LoanState loanState);

  List<Loan> findByDueDateAndLoanState(LocalDate dueDate, LoanState loanState);
}
