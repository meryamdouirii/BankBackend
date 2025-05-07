# Gebruik een image met Maven én Java 17 al geïnstalleerd
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Zet werkdirectory binnen de container
WORKDIR /app

# Kopieer alle projectbestanden naar de container
COPY . .

# Zorg dat mvnw uitvoerbaar is
RUN chmod +x mvnw

# (Optioneel) Build de app zodat je weet of alles compileert
RUN ./mvnw clean install -U

# Zet poort 8080 open (moet overeenkomen met application.properties)
EXPOSE 8080

# Start de Spring Boot app
ENTRYPOINT ["./mvnw", "spring-boot:run"]
