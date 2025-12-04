# Étape 1 : image Java (tu peux passer à 17-jre-alpine pour plus léger)
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : dossier de travail
WORKDIR /app

# Étape 3 : copier le JAR
COPY target/notification-service-0.0.1-SNAPSHOT.jar app.jar

# 🔒 Durcissement : créer un user non-root + permissions minimales
RUN addgroup -S app && adduser -S -G app app \
 && chown -R app:app /app \
 && chmod 0444 /app/app.jar

# Basculer en utilisateur non-root
USER app

# Étape 4 : exposer le port 8080 (Spring Boot)
EXPOSE 8080

# Étape 5 : lancer l'application
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]

