# Stage 1: Build the application
FROM gradle:8.6-jdk21 as builder

WORKDIR /app

# Copy gradle configuration files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the application
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM openjdk:21-slim

WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose the application's port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]