package com.lending.repository;

import com.lending.entity.DailyFee;
import com.lending.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyFeeRepository extends JpaRepository<DailyFee, Long> {
  List<DailyFee> findByProduct(Product product);
}
