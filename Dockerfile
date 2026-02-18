# Use OpenJDK 17 image
FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the project (skip tests for speed)
RUN ./mvnw clean package -DskipTests

# Expose port (Render sets PORT environment variable)
EXPOSE 8080

# Run the Spring Boot executable jar
CMD ["java", "-jar", "target/jsp-gram-0.0.1-SNAPSHOT.jar"]
