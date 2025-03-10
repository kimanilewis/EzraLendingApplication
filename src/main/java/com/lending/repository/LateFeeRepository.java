package com.lending.repository;

import com.lending.entity.LateFee;
import com.lending.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LateFeeRepository extends JpaRepository<LateFee, Long> {
  List<LateFee> findByProduct(Product product);
}
