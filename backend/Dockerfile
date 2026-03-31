# Stage 1: Build
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy the pom first
COPY pom.xml .
# Copy the source
COPY ./src ./src

# Use global maven to build since mvnw might be missing
RUN apt-get update && apt-get install -m -y maven
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Create volume for file uploads
VOLUME /tmp
RUN mkdir -p /uploads
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Security:
# 1. add a non root user
RUN useradd -m concierge
USER concierge