FROM openjdk:17-jdk-slim AS build

COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src
RUN ./mvnw package

FROM openjdk:17-oracle
WORKDIR app
RUN mkdir -p /app/input
COPY --from=build target/bank-statements-processor-jar-with-dependencies.jar bank-statements-processor.jar
ENTRYPOINT ["java", "-jar", "bank-statements-processor.jar", "/app/input"]