FROM docker.io/eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY ./build/libs/filesox-all.jar ./app.jar
USER 1000

ENTRYPOINT ["java", "-jar", "app.jar"]