FROM eclipse-temurin:21-jre
WORKDIR /app
COPY processing-service/target/processing-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
