package com.example.notification_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {
        // Charge ton fichier JSON depuis /resources
        try (InputStream serviceAccount =
                     getClass().getResourceAsStream("/firebase-service-account.json")) {

            if (serviceAccount == null) {
                throw new IOException("Fichier firebase-service-account.json introuvable dans /resources !");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("✅ Firebase initialisé avec succès !");
            }
        }
    }
}
