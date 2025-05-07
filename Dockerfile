FROM ubuntu:latest AS build

# Installeer Java en andere tools
RUN apt-get update && \
    apt-get install -y openjdk-19-jdk maven curl unzip git && \
    apt-get clean

# Zet werkdirectory
WORKDIR /app

# Kopieer alle projectbestanden
COPY . .

# Geef rechten aan mvnw
RUN chmod +x mvnw

# Build de app (optioneel)
RUN ./mvnw clean install -U

# Zet poort open
EXPOSE 8080

# Start de app
ENTRYPOINT ["./mvnw", "spring-boot:run"]
