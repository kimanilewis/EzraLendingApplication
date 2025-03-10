package com.lending.notification.interfaces;

import com.lending.entity.Notification;

public interface NotificationSender {
  /**
   * Send the notification with an optional subject.
   *
   * @param notification the notification to send
   * @param subject subject text (for channels like email)
   */
  void send(Notification notification, String subject);
}
