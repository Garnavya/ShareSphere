# Multi-stage Dockerfile for ShareSphere Spring Boot Backend
# Stage 1: Build JAR using Maven
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Pre-cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile and package application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal JRE runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

EXPOSE 5000

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
