FROM sapmachine:17.0.10

# Set the working directory in the Docker image
WORKDIR /app

# Copy the entire target directory
COPY target /app

# Expose the port the application runs on
EXPOSE 8700

# Run the application
# Ensure to update the path to the jar if it's different in the target directory
ENTRYPOINT ["java", "--add-opens", "java.base/java.io=ALL-UNNAMED", "--add-exports", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-exports", "java.base/sun.security.action=ALL-UNNAMED", "-jar", "/app/tasks-processing-service-1.0-SNAPSHOT.jar"]