package com.lending.repository;

import com.lending.entity.NotificationTemplate;
import com.lending.model.NotificationChannel;
import com.lending.model.NotificationEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
  List<NotificationTemplate> findByEventTypeAndActive(
      NotificationEventType eventType, Boolean active);

  Optional<NotificationTemplate> findByEventTypeAndChannelAndActive(
      NotificationEventType eventType, NotificationChannel channel, Boolean active);
}
