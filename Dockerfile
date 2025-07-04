# Use a lightweight OpenJDK image
FROM openjdk:21-jdk-slim

## Set environment variables
#ENV SPRING_PROFILES_ACTIVE=prod

# Set the working directory
WORKDIR /app

# Copy the jar file into the container (replace with your actual jar file name)
COPY target/employee-management-webapp-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
