FROM openjdk:21-slim

# Set the working directory in the container
WORKDIR /app

# Copy the build files to the container
COPY build/libs/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]