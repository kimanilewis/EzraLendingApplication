package com.lending.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

  @Value("${firebase.service-account.path}")
  private String serviceAccountPath;

  @Value("${firebase.project-id}")
  private String projectId;

  @Bean
  public FirebaseApp firebaseApp() throws IOException {
    GoogleCredentials credentials =
        GoogleCredentials.fromStream(new ClassPathResource(serviceAccountPath).getInputStream());

    FirebaseOptions options =
        FirebaseOptions.builder().setCredentials(credentials).setProjectId(projectId).build();

    return FirebaseApp.initializeApp(options);
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
    return FirebaseMessaging.getInstance(firebaseApp);
  }
}
