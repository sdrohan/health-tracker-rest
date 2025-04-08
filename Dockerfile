# Use a lightweight JDK base image
FROM eclipse-temurin:17-jdk

# Set working directory inside container
WORKDIR /app

# Copy the JAR file from build context to container
COPY target/health-tracker-rest-1.0-jar-with-dependencies.jar app.jar

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]