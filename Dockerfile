# Use OpenJDK 17 slim image (bullseye variant)
FROM openjdk:17-jdk-slim-bullseye

# Set working directory
WORKDIR /app

# Copy Maven wrapper and project files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the project (skip tests)
RUN ./mvnw clean package -DskipTests

# Expose the same port your app uses
EXPOSE 8080

# Run the executable jar
CMD ["java", "-jar", "target/jsp-gram-0.0.1-SNAPSHOT.jar"]
