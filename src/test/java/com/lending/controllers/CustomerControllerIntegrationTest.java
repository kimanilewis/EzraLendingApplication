package com.lending.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lending.controller.CustomerController;
import com.lending.dto.CustomerRequest;
import com.lending.dto.LoanLimitRequest;
import com.lending.entity.Customer;
import com.lending.entity.LoanLimit;
import com.lending.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CustomerService customerService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testCreateCustomer() throws Exception {
    CustomerRequest request =
        CustomerRequest.builder()
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .phone("1234567890")
            .address("123 Main St")
            .dateOfBirth(LocalDate.of(1990, 1, 1))
            .creditScore(750)
            .monthlyIncome(new BigDecimal("5000.00"))
            .build();

    Customer customer =
        Customer.builder()
            .id(1L)
            .firstName("Jane")
            .lastName("Doe")
            .email("jane.doe@example.com")
            .build();

    Mockito.when(customerService.createCustomer(Mockito.any(CustomerRequest.class)))
        .thenReturn(customer);

    mockMvc
        .perform(
            post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.firstName").value("Jane"));
  }

  @Test
  public void testGetCustomerById() throws Exception {
    Customer customer = Customer.builder().id(1L).firstName("Jane").lastName("Doe").build();
    Mockito.when(customerService.getCustomerById(1L)).thenReturn(customer);

    mockMvc
        .perform(get("/api/customers/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.firstName").value("Jane"));
  }

  @Test
  public void testSetLoanLimit() throws Exception {
    LoanLimitRequest request =
        LoanLimitRequest.builder()
            .productId(1L)
            .customerId(2L)
            .maxAmount(new BigDecimal("15000.00"))
            .build();

    LoanLimit loanLimit = LoanLimit.builder().id(1L).build();
    Mockito.when(customerService.setLoanLimit(Mockito.eq(1L), Mockito.any(LoanLimitRequest.class)))
        .thenReturn(loanLimit);

    mockMvc
        .perform(
            post("/api/customers/1/loan-limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }
}
