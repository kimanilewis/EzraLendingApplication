package com.lending.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lending.controller.ProductController;
import com.lending.dto.ProductRequest;
import com.lending.dto.ProductResponse;
import com.lending.model.TenureType;
import com.lending.service.ProductService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
public class ProductControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ProductService productService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testCreateProduct() throws Exception {
    ProductRequest request =
        ProductRequest.builder()
            .name("Test Product")
            .description("Description of test product")
            .tenure(12)
            .tenureType(TenureType.MONTHS)
            .interestRate(new BigDecimal("5.5"))
            .isInstallmentLoan(true)
            .daysAfterDueForFee(5)
            .build();

    ProductResponse productResponse =
        ProductResponse.builder()
            .id(1L)
            .name(request.getName())
            .description(request.getDescription())
            .tenure(request.getTenure())
            .tenureType(request.getTenureType())
            .interestRate(request.getInterestRate())
            .daysAfterDueForFee(request.getDaysAfterDueForFee())
            .build();

    Mockito.when(productService.createProduct(Mockito.any(ProductRequest.class)))
        .thenReturn(productResponse);

    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Test Product"));
  }

  @Test
  public void testGetProductById() throws Exception {
    ProductResponse productResponse = ProductResponse.builder().id(1L).name("Test Product").build();
    Mockito.when(productService.getProductById(1L)).thenReturn(productResponse);

    mockMvc
        .perform(get("/api/products/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Test Product"));
  }

  @Test
  public void testUpdateProduct() throws Exception {
    ProductRequest request =
        ProductRequest.builder()
            .name("Updated Product")
            .description("Updated Description")
            .tenure(24)
            .tenureType(TenureType.MONTHS)
            .interestRate(new BigDecimal("6.5"))
            .isInstallmentLoan(true)
            .daysAfterDueForFee(7)
            .build();

    ProductResponse productResponse =
        ProductResponse.builder()
            .id(1L)
            .name("Updated Product")
            .description("Updated Description")
            .tenure(24)
            .tenureType(TenureType.MONTHS)
            .interestRate(new BigDecimal("6.5"))
            .daysAfterDueForFee(7)
            .build();

    Mockito.when(productService.updateProduct(Mockito.eq(1L), Mockito.any(ProductRequest.class)))
        .thenReturn(productResponse);

    mockMvc
        .perform(
            put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Product"));
  }
}
