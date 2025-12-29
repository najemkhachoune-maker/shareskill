# ----------- Build Stage ------------
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copier pom.xml et télécharger les dépendances (optimisation du cache)
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copier le code source
COPY src ./src

# Build l'application (skip tests pour CI/CD)
RUN mvn -B clean package -DskipTests

# ----------- Runtime Stage (DISTROLESS) ------------
FROM gcr.io/distroless/java17:latest
WORKDIR /app

# Copier le JAR généré
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Exécuter l'application
CMD ["java","-jar","app.jar"]

