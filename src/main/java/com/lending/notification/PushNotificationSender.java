package com.lending.notification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.lending.notification.interfaces.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PushNotificationSender implements NotificationSender {
  private final FirebaseMessaging firebaseMessaging;

  public PushNotificationSender(FirebaseMessaging firebaseMessaging) {
    this.firebaseMessaging = firebaseMessaging;
  }

  @Override
  public void send(com.lending.entity.Notification notification, String subject) {
    String pushToken = notification.getCustomer().getPushToken();
    String content = notification.getContent();

    log.info("Sending Push Notification to customer ID: {}", notification.getCustomer().getId());

    if (pushToken == null || pushToken.isEmpty()) {
      log.error("No push token found for customer ID: {}", notification.getCustomer().getId());
      return;
    }

    Message message = Message.builder()
        .setToken(pushToken)
        .setNotification(
            com.google.firebase.messaging.Notification.builder()
                .setTitle(subject)
                .setBody(content)
                .build()
        )
        .build();

    try {
      String response = firebaseMessaging.send(message);
      log.info("Push notification sent successfully. Response: {}", response);
    } catch (FirebaseMessagingException ex) {
      log.error("Failed to send push notification to customer ID: {}", notification.getCustomer().getId(), ex);
    }
  }
}