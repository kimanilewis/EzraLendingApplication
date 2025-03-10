package com.lending.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.lending.dto.ProductRequest;
import com.lending.dto.ProductResponse;
import com.lending.entity.Product;
import com.lending.model.TenureType;
import com.lending.repository.ProductRepository;
import com.lending.service.ProductService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductServiceTest {

  @Mock private ProductRepository productRepository;

  @InjectMocks private ProductService productService;

  public ProductServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testCreateProduct() {
    // Given
    ProductRequest request =
        ProductRequest.builder()
            .name("Test Loan Product")
            .description("Test Description")
            .tenure(12)
            .tenureType(TenureType.MONTHS)
            .interestRate(new BigDecimal("5.50"))
            .isInstallmentLoan(true)
            .daysAfterDueForFee(5)
            .build();

    Product product =
        Product.builder()
            .id(1L)
            .name(request.getName())
            .description(request.getDescription())
            .tenure(request.getTenure())
            .tenureType(request.getTenureType())
            .interestRate(request.getInterestRate())
            .isInstallmentLoan(request.getIsInstallmentLoan())
            .daysAfterDueForFee(request.getDaysAfterDueForFee())
            .active(true)
            .build();

    when(productRepository.save(any(Product.class))).thenReturn(product);

    // When
    ProductResponse created = productService.createProduct(request);

    // Then
    assertThat(created).isNotNull();
    assertThat(created.getId()).isEqualTo(1L);
    assertThat(created.getName()).isEqualTo("Test Loan Product");
  }
}
