# Этап сборки
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package

# Финальный образ
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/WalletApp-1.0-SNAPSHOT.jar .
EXPOSE 8080
CMD java $JAVA_OPTS -jar WalletApp-1.0-SNAPSHOT.jar