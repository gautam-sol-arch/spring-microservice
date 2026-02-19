# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy common-service first
COPY common-service ./common-service

# Build common-service and install to Maven repository
RUN cd common-service && mvn clean install -DskipTests

# Copy loan-service source
COPY loan-service/src ./loan-service/src
COPY loan-service/pom.xml ./loan-service/

# Change to loan-service directory
WORKDIR /app/loan-service

# Build loan-service (now it can find common-service in Maven repo)
RUN mvn clean package -DskipTests

# Use smaller runtime image
FROM eclipse-temurin:21-jre-alpine

# Install wget for health checks
RUN apk add --no-cache wget

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/loan-service/target/loan-service-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
