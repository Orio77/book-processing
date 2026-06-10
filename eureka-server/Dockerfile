# syntax=docker/dockerfile:1
# Build stage — run from the repo root: docker build -f eureka-server/Dockerfile .
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY shared-common/pom.xml shared-common/
COPY shared-proto/pom.xml shared-proto/
COPY books-service/pom.xml books-service/
COPY auth-service/pom.xml auth-service/
COPY processing-service/pom.xml processing-service/
COPY eureka-server/pom.xml eureka-server/
COPY api-gateway/pom.xml api-gateway/
RUN --mount=type=cache,target=/root/.m2 mvn -q -pl eureka-server -am dependency:go-offline
COPY eureka-server eureka-server
RUN --mount=type=cache,target=/root/.m2 mvn -q -pl eureka-server -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/eureka-server/target/eureka-server-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]