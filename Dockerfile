# Этап сборки с кешированием Maven
FROM maven:3.9.4-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Настроить альтернативные репозитории
RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyun</id><mirrorOf>central</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

# Копируем только POM сначала для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем проект
COPY src ./src
RUN mvn clean package -DskipTests

# Остальная часть без изменений...
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

ENV JAVA_TOOL_OPTIONS="-XX:+UseG1GC -XX:+UseContainerSupport"

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]