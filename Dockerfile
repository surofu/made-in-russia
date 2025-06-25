# Этап сборки с кешированием Maven
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем только POM сначала для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests -T 1C -Dmaven.test.skip=true -Dmaven.compile.fork=true

# Этап создания многослойного образа
FROM eclipse-temurin:17.0.6_10-jre-jammy AS builder
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# Финальный образ
FROM eclipse-temurin:17.0.6_10-jre-jammy
WORKDIR /app

# Копируем слои в правильном порядке для лучшего кеширования
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

# Оптимизированные JVM параметры для контейнеризации (исправленная версия)
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70.0 -XX:InitialRAMPercentage=40.0 -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/heapdumps -Djava.security.egd=file:/dev/./urandom -Dspring.jmx.enabled=false -Dfile.encoding=UTF-8"

# Создаем директорию для дампов памяти
RUN mkdir /heapdumps && chmod 777 /heapdumps

# Ограничиваем количество потоков для Tomcat
ENV CATALINA_OPTS="-XX:ActiveProcessorCount=2"

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]