package com.lending.repository;

import com.lending.entity.ServiceFee;
import com.lending.model.product.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceFeeRepository extends JpaRepository<ServiceFee, Long> {
  List<ServiceFee> findByProduct(Product product);
}
