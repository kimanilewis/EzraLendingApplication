package com.lending.controller;

import com.lending.dto.ProductRequest;
import com.lending.dto.ProductResponse;
import com.lending.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

  @Autowired private ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
    ProductResponse response = productService.createProduct(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
    ProductResponse response = productService.getProductById(id);
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    List<ProductResponse> products = productService.getAllProducts();
    return ResponseEntity.ok(products);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductRequest request) {
    ProductResponse product = productService.updateProduct(id, request);
    return ResponseEntity.ok(product);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
