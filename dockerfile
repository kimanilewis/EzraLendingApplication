# Build stage
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app

# Copy pom.xml first to leverage Docker cache
COPY pom.xml .
# Copy maven wrapper if you're using it
COPY .mvn .mvn
COPY mvnw* ./

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]