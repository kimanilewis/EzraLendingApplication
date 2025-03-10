package com.lending.service;

import com.lending.controller.exceptions.ResourceNotFoundException;
import com.lending.dto.ProductRequest;
import com.lending.dto.ProductResponse;
import com.lending.entity.DailyFee;
import com.lending.entity.LateFee;
import com.lending.entity.Product;
import com.lending.entity.ServiceFee;
import com.lending.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;

  @Autowired
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Transactional(readOnly = true)
  public List<ProductResponse> getAllProducts() {
    return productRepository.findAll().stream()
        .map(this::mapToProductResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public ProductResponse getProductById(Long id) {
    Product product = findProductById(id);
    return mapToProductResponse(product);
  }

  @Transactional
  public ProductResponse createProduct(ProductRequest productRequest) {
    Product product = mapToProduct(productRequest);
    Product savedProduct = productRepository.save(product);
    return mapToProductResponse(savedProduct);
  }

  @Transactional
  public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
    Product existingProduct = findProductById(id);
    updateProductFromRequest(existingProduct, productRequest);
    Product updatedProduct = productRepository.save(existingProduct);
    return mapToProductResponse(updatedProduct);
  }

  @Transactional
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      throw new ResourceNotFoundException("Product not found with id: " + id);
    }
    productRepository.deleteById(id);
  }

  private Product findProductById(Long id) {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
  }

  private Product mapToProduct(ProductRequest request) {
    Product product = new Product();
    // Map fields from request to product entity
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setTenure(request.getTenure());
    product.setTenureType(request.getTenureType());
    product.setInterestRate(request.getInterestRate());
    product.setIsInstallmentLoan(request.getIsInstallmentLoan());
    product.setDaysAfterDueForFee(request.getDaysAfterDueForFee());

    // Map fees
    if (request.getServiceFees() != null) {
      List<ServiceFee> serviceFees =
          request.getServiceFees().stream()
              .map(
                  feeReq ->
                      ServiceFee.builder()
                          .name(feeReq.getName())
                          .feeAmount(feeReq.getFeeAmount())
                          .feePercentage(feeReq.getFeePercentage())
                          .isPercentage(feeReq.getIsPercentage())
                          .applyAtOrigination(feeReq.getApplyAtOrigination())
                          .product(product)
                          .build())
              .collect(Collectors.toList());
      product.setServiceFees(serviceFees);
    }

    if (request.getDailyFees() != null) {
      List<DailyFee> dailyFees =
          request.getDailyFees().stream()
              .map(
                  feeReq ->
                      DailyFee.builder()
                          .name(feeReq.getName())
                          .feeAmount(feeReq.getFeeAmount())
                          .applyWhenOverdue(feeReq.getApplyWhenOverdue())
                          .product(product)
                          .build())
              .collect(Collectors.toList());
      product.setDailyFees(dailyFees);
    }
    if (request.getLateFees() != null) {
      List<LateFee> lateFees =
          request.getLateFees().stream()
              .map(
                  feeReq ->
                      LateFee.builder()
                          .name(feeReq.getName())
                          .feeAmount(feeReq.getFeeAmount())
                          .feePercentage(feeReq.getFeePercentage())
                          .isPercentage(feeReq.getIsPercentage())
                          .triggerDaysAfterDue(feeReq.getTriggerDaysAfterDue())
                          .product(product)
                          .build())
              .collect(Collectors.toList());
      product.setLateFees(lateFees);
    }

    return product;
  }

  private void updateProductFromRequest(Product product, ProductRequest request) {
    // Update product fields from request
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setTenure(request.getTenure());
    product.setTenureType(request.getTenureType());
    product.setInterestRate(request.getInterestRate());
    product.setDaysAfterDueForFee(request.getDaysAfterDueForFee());

    if (request.getServiceFees() != null) {
      product.getServiceFees().clear();
      product
          .getServiceFees()
          .addAll(
              request.getServiceFees().stream()
                  .map(
                      feeReq ->
                          ServiceFee.builder()
                              .name(feeReq.getName())
                              .feeAmount(feeReq.getFeeAmount())
                              .feePercentage(feeReq.getFeePercentage())
                              .isPercentage(feeReq.getIsPercentage())
                              .applyAtOrigination(feeReq.getApplyAtOrigination())
                              .product(product)
                              .build())
                  .toList());
    }
    if (request.getDailyFees() != null) {
      product.getDailyFees().clear();
      product
          .getDailyFees()
          .addAll(
              request.getDailyFees().stream()
                  .map(
                      feeReq ->
                          DailyFee.builder()
                              .name(feeReq.getName())
                              .feeAmount(feeReq.getFeeAmount())
                              .applyWhenOverdue(feeReq.getApplyWhenOverdue())
                              .product(product)
                              .build())
                  .toList());
    }
    if (request.getLateFees() != null) {
      product.getLateFees().clear();
      product
          .getLateFees()
          .addAll(
              request.getLateFees().stream()
                  .map(
                      feeReq ->
                          LateFee.builder()
                              .name(feeReq.getName())
                              .feeAmount(feeReq.getFeeAmount())
                              .feePercentage(feeReq.getFeePercentage())
                              .isPercentage(feeReq.getIsPercentage())
                              .triggerDaysAfterDue(feeReq.getTriggerDaysAfterDue())
                              .product(product)
                              .build())
                  .toList());
    }
  }

  private ProductResponse mapToProductResponse(Product product) {
    ProductResponse response = new ProductResponse();
    response.setId(product.getId());
    response.setName(product.getName());
    response.setDescription(product.getDescription());
    response.setTenure(product.getTenure());
    response.setTenureType(product.getTenureType());
    response.setInterestRate(product.getInterestRate());
    response.setDaysAfterDueForFee(product.getDaysAfterDueForFee());

    // Map fees to response objects
    if (product.getServiceFees() != null) {
      response.setServiceFees(
          product.getServiceFees().stream()
              .map(
                  fee ->
                      ServiceFee.builder()
                          .feeAmount(fee.getFeeAmount())
                          .name(fee.getName())
                          .build())
              .toList());
    }

    if (product.getDailyFees() != null) {
      response.setDailyFees(
          product.getDailyFees().stream()
              .map(
                  fee ->
                      DailyFee.builder().feeAmount(fee.getFeeAmount()).name(fee.getName()).build())
              .toList());
    }

    if (product.getLateFees() != null) {
      response.setLateFees(
          product.getLateFees().stream()
              .map(
                  fee ->
                      LateFee.builder().feeAmount(fee.getFeeAmount()).name(fee.getName()).build())
              .collect(Collectors.toList()));
    }

    return response;
  }
}
