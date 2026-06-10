# syntax=docker/dockerfile:1
# Build stage — run from the repo root: docker build -f api-gateway/Dockerfile .
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
RUN --mount=type=cache,target=/root/.m2 mvn -q -pl api-gateway -am dependency:go-offline
COPY api-gateway api-gateway
RUN --mount=type=cache,target=/root/.m2 mvn -q -pl api-gateway -am package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/api-gateway/target/api-gateway-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]