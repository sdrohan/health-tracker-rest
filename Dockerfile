# Use a lightweight JDK base image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy the JAR file from build context to container
COPY target/health-tracker-rest-1.0-with-dependencies.jar health-tracker-rest-1.0-with-dependencies.jar

# Start the app
ENTRYPOINT ["java", "-jar", "health-tracker-rest-1.0-with-dependencies.jar"]