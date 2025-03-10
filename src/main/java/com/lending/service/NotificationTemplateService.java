package com.lending.service;

import com.lending.dto.NotificationTemplateRequest;
import com.lending.entity.NotificationTemplate;
import com.lending.repository.NotificationTemplateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationTemplateService {

  private final NotificationTemplateRepository notificationTemplateRepository;

  @Transactional
  public NotificationTemplate createTemplate(NotificationTemplateRequest request) {
    NotificationTemplate template =
        NotificationTemplate.builder()
            .name(request.getName())
            .eventType(request.getEventType())
            .channel(request.getChannel())
            .subject(request.getSubject())
            .templateContent(request.getTemplateContent())
            .active(true)
            .build();
    return notificationTemplateRepository.save(template);
  }

  public List<NotificationTemplate> getAllTemplates() {
    return notificationTemplateRepository.findAll();
  }
}
