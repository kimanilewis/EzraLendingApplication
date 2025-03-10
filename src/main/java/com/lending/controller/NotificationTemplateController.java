package com.lending.controller;

import com.lending.dto.NotificationTemplateRequest;
import com.lending.entity.NotificationTemplate;
import com.lending.service.NotificationTemplateService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification-templates")
@Validated
public class NotificationTemplateController {

  @Autowired private NotificationTemplateService notificationTemplateService;

  @PostMapping
  public ResponseEntity<NotificationTemplate> createTemplate(
      @Valid @RequestBody NotificationTemplateRequest request) {
    NotificationTemplate template = notificationTemplateService.createTemplate(request);
    return new ResponseEntity<>(template, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<NotificationTemplate>> getAllTemplates() {
    List<NotificationTemplate> templates = notificationTemplateService.getAllTemplates();
    return ResponseEntity.ok(templates);
  }
}
