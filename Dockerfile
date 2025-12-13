FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Установка cwebp и curl в Alpine Linux
RUN apk update && apk add --no-cache libwebp-tools

COPY target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseContainerSupport"
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]