FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean install -U -DskipTests
EXPOSE 8080
ENTRYPOINT ["./mvnw", "spring-boot:run"]
