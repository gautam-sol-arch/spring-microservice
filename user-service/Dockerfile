# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the entire backend directory to have access to common-service
COPY ../ .

# Build common-service first and install to Maven repository
RUN cd common-service && mvn clean install -DskipTests

# Change to user-service directory
WORKDIR /app/user-service

# Build user-service (now it can find common-service in Maven repo)
RUN mvn clean package -DskipTests

# Use smaller runtime image
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8082

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
