FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia tutto il progetto
COPY . .

# Esegui il build
RUN ./mvnw clean package -DskipTests

# Espone la porta 8080
EXPOSE 8080

# Avvia il jar generato
CMD ["java", "-jar", "target/nichenetwork-backend-0.0.1-SNAPSHOT.jar"]
