# Stage 1: Build the application artifact
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Set the working directory
WORKDIR /app

# Copy pom.xml and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use an official OpenJDK image to run the application
from openjdk:21-ea-23-jdk-bullseye

WORKDIR /app

# copy the build JAR file from the build stage
COPY --from=build /app/target/hotelManagementSystem-0.0.1-SNAPSHOT.jar .

# Expose port 8080
EXPOSE 8080

# Specify the command to run the application
ENTRYPOINT ["java","-jar","/app/hotelManagementSystem-0.0.1-SNAPSHOT.jar"]
