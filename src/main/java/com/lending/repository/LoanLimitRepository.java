package com.lending.repository;

import com.lending.entity.Customer;
import com.lending.entity.LoanLimit;
import com.lending.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanLimitRepository extends JpaRepository<LoanLimit, Long> {
  List<LoanLimit> findByCustomerId(Long id);

  Optional<LoanLimit> findByCustomerAndProduct(Customer customer, Product product);

  Optional<LoanLimit> findByCustomerIdAndProductId(Long customerId, Long productId);
}
