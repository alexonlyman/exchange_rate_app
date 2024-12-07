FROM openjdk:17-jdk-slim
WORKDIR /app
EXPOSE 8080
COPY target/exchange_rate_app-0.0.1-SNAPSHOT.jar /app/exchange_rate_app-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "exchange_rate_app-0.0.1-SNAPSHOT.jar"]