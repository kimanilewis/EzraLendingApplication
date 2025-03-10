package com.lending.notification;

import com.lending.entity.Notification;
import com.lending.notification.interfaces.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SmsNotificationSender implements NotificationSender {
  @Override
  public void send(Notification notification, String subject) {
    // integrate with AfricaStalking SMS provider.
    log.info("SMS sent to " + notification.getCustomer().getPhone());
  }
}
