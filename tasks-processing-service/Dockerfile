# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim as build

# Set the working directory in the Docker image
WORKDIR /app

# Copy the custom library JARs
COPY shared_libraries/*.jar /usr/share/maven/ref/

# Install custom libraries to the local Maven repository
RUN mvn install:install-file -Dfile=/usr/share/maven/ref/shared-events-library-1.0-SNAPSHOT.jar -DgroupId=com.wenox.anonymization -DartifactId=shared-events-library -Dversion=1.0-SNAPSHOT -Dpackaging=jar

# Improve build cache by downloading dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the project source files
COPY src /app/src

# Build the application without running tests for faster build
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim

# Copy the built application from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8700

# Run the application
ENTRYPOINT ["java", "--add-opens", "java.base/java.io=ALL-UNNAMED", "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-exports", "java.base/sun.security.action=ALL-UNNAMED", "-jar", "/app.jar"]

