# Этап сборки с кешированием Maven
FROM maven:3.9.6-openjdk-21 AS build
WORKDIR /app

# Копируем только POM сначала для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests -T 1C

# Этап создания многослойного образа
FROM openjdk:21-jdk-slim AS builder
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Финальный образ
FROM openjdk:21-jdk-slim
WORKDIR /app

# Устанавливаем curl для healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Копируем слои в правильном порядке для лучшего кеширования
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# Оптимизированные JVM параметры для контейнеризации
ENV JAVA_TOOL_OPTIONS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+TieredCompilation -XX:+AlwaysPreTouch -XX:+UnlockExperimentalVMOptions -XX:+UseTransparentHugePages"

# Создаем директорию для дампов памяти
RUN mkdir /heapdumps && chmod 777 /heapdumps

# Ограничиваем количество потоков для Tomcat
ENV CATALINA_OPTS="-XX:ActiveProcessorCount=2"

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]