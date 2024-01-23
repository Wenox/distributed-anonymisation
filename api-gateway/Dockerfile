# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim as build

# Set the working directory in the Docker image
WORKDIR /app

# Improve build cache by downloading dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the project source files
COPY src /app/src

# Build the application without running tests for faster build
RUN mvn clean package -DskipTests -X

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Copy the built application from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
