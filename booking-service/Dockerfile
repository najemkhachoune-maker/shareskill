FROM eclipse-temurin:17-jre
WORKDIR /app
# Use a dedicated writable place for H2
RUN mkdir -p /data
VOLUME ["/data"]
# Copy the jar you just built with Maven on Windows
COPY target/booking-service-0.0.1-SNAPSHOT.jar /app/booking-service.jar
EXPOSE 8080
# Force H2 file to live under /data inside container
ENV SPRING_DATASOURCE_URL=jdbc:h2:file:/data/bookingdb;AUTO_SERVER=FALSE;DB_CLOSE_ON_EXIT=FALSE
ENV SPRING_H2_CONSOLE_ENABLED=true
ENTRYPOINT ["java","-jar","/app/booking-service.jar"]
