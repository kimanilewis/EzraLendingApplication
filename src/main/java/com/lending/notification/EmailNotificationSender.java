package com.lending.notification;

import com.lending.entity.Notification;
import com.lending.notification.interfaces.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailNotificationSender implements NotificationSender {

  private final JavaMailSender mailSender;

  public EmailNotificationSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void send(Notification notification, String subject) {
    log.info(
        "Sending email notification to {}: {}",
        notification.getCustomer().getEmail(),
        notification.getId());
    String recipientEmail = notification.getCustomer().getEmail();
    log.info("Sending email notification to {}: {}", recipientEmail, notification.getId());

    try {
      MimeMessagePreparator messagePreparator =
          mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(notification.getContent(), false); // Adjust if using HTML
          };
      mailSender.send(messagePreparator);
      log.info("Email sent successfully to {}", recipientEmail);
    } catch (MailException ex) {
      log.error("Failed to send email to {}", recipientEmail, ex);
    }
  }
}
