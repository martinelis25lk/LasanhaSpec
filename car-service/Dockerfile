# cache-bust: 9
# -------- STAGE 1: build --------
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

ARG CACHEBUST=9
COPY . .

RUN mvn clean package -DskipTests

# -------- STAGE 2: runtime --------
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]