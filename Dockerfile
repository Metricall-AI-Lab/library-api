# Build with Maven, run on a small JRE. curl is kept in the runtime image for the smoke tests.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B -DskipTests package

FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /src/target/library-api-0.1.0.jar app.jar
COPY tests tests
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
