# Этап сборки с кешированием Maven
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Копируем только POM сначала для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B -T1

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests -T 1C

# Этап создания многослойного образа
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Финальный образ
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Копируем слои в правильном порядке для лучшего кеширования
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# Оптимизированные JVM параметры для контейнеризации
ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseContainerSupport"

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]