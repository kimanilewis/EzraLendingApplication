package com.lending.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lending.controller.LoanController;
import com.lending.dto.LoanRequest;
import com.lending.dto.LoanResponse;
import com.lending.entity.Loan;
import com.lending.service.LoanService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoanController.class)
public class LoanControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private LoanService loanService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testCreateLoan() throws Exception {
    // Given a LoanRequest payload
    LoanRequest request =
        LoanRequest.builder()
            .customerId(1L)
            .productId(1L)
            .loanAmount(new BigDecimal("10000.00"))
            .originationDate(LocalDate.now())
            .numberOfInstallments(12)
            .build();

    Loan loan =
        Loan.builder()
            .id(1L)
            .loanAmount(request.getLoanAmount())
            .originationDate(request.getOriginationDate())
            .build();
    Mockito.when(loanService.createLoan(Mockito.any(LoanRequest.class)))
        .thenReturn(LoanResponse.builder().build());

    // When we POST /api/loans
    mockMvc
        .perform(
            post("/api/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }
}
