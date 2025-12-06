package com.example.notification_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests de démarrage du microservice Notification.
 * - Désactive Firebase pour les tests (firebase.enabled=false)
 * - Utilise le profil "test"
 * - Vérifie le chargement du contexte et exécute main() pour générer de la couverture.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = { "firebase.enabled=false" }
)
@ActiveProfiles("test")
class NotificationServiceApplicationTest {

    /** Vérifie que le contexte Spring se charge correctement (sans Firebase). */
    @Test
    void contextLoads() {
        // Si le contexte échoue à démarrer, ce test échouera.
    }

    /** Exécute la méthode main() pour garantir un minimum de couverture JaCoCo. */
    @Test
    void main_runs_without_error() {
        assertDoesNotThrow(() -> NotificationServiceApplication.main(new String[]{}));
    }
}
