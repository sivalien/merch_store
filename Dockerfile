FROM openjdk:17-jdk-slim

COPY build/libs/merch_store-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]