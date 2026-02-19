# Use Maven image for building
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use smaller runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/config-server-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8888

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
