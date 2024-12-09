FROM docker.io/gradle:8-jdk21 AS builder

WORKDIR /app

COPY . .

RUN gradle build

FROM docker.io/eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/filesox-all.jar ./app.jar

USER 1000
ENTRYPOINT ["java", "-jar", "app.jar"]