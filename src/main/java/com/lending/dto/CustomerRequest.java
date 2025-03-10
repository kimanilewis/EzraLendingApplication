package com.lending.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {

  @NotBlank(message = "First name is required")
  @Size(max = 50, message = "First name cannot exceed 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(max = 50, message = "Last name cannot exceed 50 characters")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Invalid email format")
  @Size(max = 100, message = "Email cannot exceed 100 characters")
  private String email;

  @NotBlank(message = "Phone is required")
  @Size(max = 20, message = "Phone cannot exceed 20 characters")
  private String phone;

  @Size(max = 255, message = "Address cannot exceed 255 characters")
  private String address;

  @Past(message = "Date of birth must be in the past")
  private LocalDate dateOfBirth;

  @Min(value = 300, message = "Credit score must be at least 300")
  @Max(value = 850, message = "Credit score cannot exceed 850")
  private Integer creditScore;

  @PositiveOrZero(message = "Monthly income must be non-negative")
  private BigDecimal monthlyIncome;
}
