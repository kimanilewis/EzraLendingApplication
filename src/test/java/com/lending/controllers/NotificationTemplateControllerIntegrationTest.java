package com.lending.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lending.controller.NotificationTemplateController;
import com.lending.dto.NotificationTemplateRequest;
import com.lending.entity.NotificationTemplate;
import com.lending.model.NotificationChannel;
import com.lending.model.NotificationEventType;
import com.lending.service.NotificationTemplateService;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationTemplateController.class)
public class NotificationTemplateControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private NotificationTemplateService notificationTemplateService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testCreateNotificationTemplate() throws Exception {
    NotificationTemplateRequest request =
        NotificationTemplateRequest.builder()
            .name("Loan Created Template")
            .eventType(NotificationEventType.LOAN_CREATED)
            .channel(NotificationChannel.EMAIL)
            .subject("Loan Approved")
            .templateContent("Hello {{firstName}}, your loan has been approved.")
            .build();

    NotificationTemplate template =
        NotificationTemplate.builder()
            .id(1L)
            .name(request.getName())
            .eventType(request.getEventType())
            .channel(request.getChannel())
            .subject(request.getSubject())
            .templateContent(request.getTemplateContent())
            .active(true)
            .build();

    Mockito.when(
            notificationTemplateService.createTemplate(
                Mockito.any(NotificationTemplateRequest.class)))
        .thenReturn(template);

    mockMvc
        .perform(
            post("/api/notification-templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Loan Created Template"));
  }

  @Test
  public void testGetAllNotificationTemplates() throws Exception {
    NotificationTemplate template =
        NotificationTemplate.builder().id(1L).name("Loan Created Template").build();
    Mockito.when(notificationTemplateService.getAllTemplates())
        .thenReturn(Collections.singletonList(template));

    mockMvc
        .perform(get("/api/notification-templates"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].name").value("Loan Created Template"));
  }
}
