package com.lending.util;

import com.lending.controller.exceptions.ResourceNotFoundException;
import com.lending.entity.Customer;
import com.lending.entity.Product;
import com.lending.repository.CustomerRepository;
import com.lending.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {

  private static ProductRepository productRepository;
  private static CustomerRepository customerRepository;

  public static Customer validateCustomerExists(Long customerId) {
    return customerRepository
        .findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
  }

  public static Product validateProductExistsAndActive(Long productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    if (!product.getActive()) {
      throw new IllegalStateException("Product is not active");
    }
    return product;
  }
}
