# Этап сборки с оптимизацией Maven
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Увеличиваем память для Maven сборки
ENV MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"

# Копируем только POM для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress

# Копируем исходники и собираем с оптимизацией
COPY src ./src
RUN mvn clean package -DskipTests -T 2C \
    --no-transfer-progress \
    -Dmaven.test.skip=true \
    -Dmaven.compile.fork=true \
    -Dmaven.compiler.maxmem=1g

# Этап извлечения слоев JAR
FROM eclipse-temurin:17.0.10_7-jre-alpine AS layers
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Проверяем наличие layertools
RUN java -Djarmode=layertools -jar app.jar list > /dev/null 2>&1 || \
    (echo "LayerTools not available, using single layer" && mkdir -p dependencies spring-boot-loader application && \
     cp app.jar application/)

# Извлекаем слои если доступно
RUN java -Djarmode=layertools -jar app.jar extract || true

# Финальный оптимизированный образ
FROM eclipse-temurin:17.0.10_7-jre-alpine
WORKDIR /app

# Установка дополнительных пакетов для мониторинга
RUN apk add --no-cache curl dumb-init

# Создание пользователя для безопасности
RUN addgroup -g 1001 spring && \
    adduser -D -s /bin/sh -u 1001 -G spring spring

# Создание необходимых директорий
RUN mkdir -p /app/logs /app/heapdumps /tmp/app && \
    chown -R spring:spring /app /tmp/app && \
    chmod 755 /app/logs /app/heapdumps

# Копирование слоев приложения
COPY --from=layers --chown=spring:spring /app/dependencies/ ./
COPY --from=layers --chown=spring:spring /app/spring-boot-loader/ ./
COPY --from=layers --chown=spring:spring /app/snapshot-dependencies/ ./
COPY --from=layers --chown=spring:spring /app/application/ ./

# Переключение на непривилегированного пользователя
USER spring

# Оптимизированные параметры JVM для контейнерной среды
ENV JAVA_TOOL_OPTIONS=" \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=70.0 \
    -XX:InitialRAMPercentage=40.0 \
    -XX:MinRAMPercentage=20.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:G1HeapRegionSize=16m \
    -XX:+G1UseAdaptiveIHOP \
    -XX:G1MixedGCCountTarget=8 \
    -XX:+UseStringDeduplication \
    -XX:+OptimizeStringConcat \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/app/heapdumps/heap-dump.hprof \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+PrintGCDetails \
    -XX:+PrintGCTimeStamps \
    -XX:+PrintGCApplicationStoppedTime \
    -Djava.security.egd=file:/dev/./urandom \
    -Djava.awt.headless=true \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=UTC"

# Дополнительные параметры для Spring Boot
ENV SPRING_PROFILES_ACTIVE=prod
ENV LOGGING_LEVEL_ROOT=WARN
ENV LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=INFO
ENV LOGGING_LEVEL_COM_SUROFU=INFO

# Оптимизация для базы данных и пула соединений
ENV SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=10
ENV SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=2
ENV SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=30000
ENV SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT=300000
ENV SPRING_DATASOURCE_HIKARI_MAX_LIFETIME=1200000
ENV SPRING_DATASOURCE_HIKARI_LEAK_DETECTION_THRESHOLD=60000

# JPA оптимизация
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=validate
ENV SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_BATCH_SIZE=25
ENV SPRING_JPA_PROPERTIES_HIBERNATE_ORDER_INSERTS=true
ENV SPRING_JPA_PROPERTIES_HIBERNATE_ORDER_UPDATES=true
ENV SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_BATCH_VERSIONED_DATA=true

# Tomcat оптимизация
ENV SERVER_TOMCAT_THREADS_MAX=100
ENV SERVER_TOMCAT_THREADS_MIN_SPARE=10
ENV SERVER_TOMCAT_ACCEPT_COUNT=50
ENV SERVER_TOMCAT_MAX_CONNECTIONS=200
ENV SERVER_TOMCAT_CONNECTION_TIMEOUT=20000

# Управление памятью приложения
ENV SPRING_JMX_ENABLED=false
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus

EXPOSE 8080

# Улучшенный health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health/readiness || exit 1

# Использование dumb-init для правильной обработки сигналов
ENTRYPOINT ["/usr/bin/dumb-init", "--"]
CMD ["java", "org.springframework.boot.loader.launch.JarLauncher"]