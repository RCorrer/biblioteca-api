# Build stage
FROM eclipse-temurin:17-jdk as build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests package || (apt-get update && apt-get install -y maven && mvn -q -DskipTests package)

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/biblioteca-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
