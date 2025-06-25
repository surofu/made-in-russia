# Этап сборки с кешированием Maven
FROM maven:3.9.6-openjdk-23 AS build
WORKDIR /app

# Копируем только POM сначала для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests -T 1C -Dmaven.test.skip=true -Dmaven.compile.fork=true

# Этап создания многослойного образа
FROM openjdk:23-jdk-slim AS builder
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Финальный образ
FROM openjdk:23-jdk-slim
WORKDIR /app

# Копируем слои в правильном порядке для лучшего кеширования
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# Оптимизированные JVM параметры для контейнеризации (исправленная версия)
ENV JAVA_TOOL_OPTIONS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseG1GC -XX:+AggressiveOpts -XX:+TieredCompilation -XX:+AlwaysPreTouch -Xnoclassgc -XX:+ClassUnloading"

# Создаем директорию для дампов памяти
RUN mkdir /heapdumps && chmod 777 /heapdumps

# Ограничиваем количество потоков для Tomcat
ENV CATALINA_OPTS="-XX:ActiveProcessorCount=2"

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]