package com.lending.dto;

import com.lending.model.NotificationChannel;
import com.lending.model.NotificationEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateRequest {

  @NotBlank(message = "Template name is required")
  @Size(max = 100, message = "Template name cannot exceed 100 characters")
  private String name;

  @NotNull(message = "Event type is required")
  private NotificationEventType eventType;

  @NotNull(message = "Channel is required")
  private NotificationChannel channel;

  @Size(max = 200, message = "Subject cannot exceed 200 characters")
  private String subject;

  @NotBlank(message = "Template content is required")
  private String templateContent;
}
