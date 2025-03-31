# Dockerfile per deploy Spring Boot su Render

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/nichenetwork-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
