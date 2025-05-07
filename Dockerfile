# Gebruik Maven + Java 21 image
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Zet werkdirectory
WORKDIR /app

# Kopieer projectbestanden
COPY . .

# Geef uitvoerrechten aan mvnw
RUN chmod +x mvnw

# Build (optioneel, valideert je code)
RUN ./mvnw clean install -U

# Zet poort open
EXPOSE 8080

# Start Spring Boot app
ENTRYPOINT ["./mvnw", "spring-boot:run"]
