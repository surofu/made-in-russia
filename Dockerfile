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

# Оптимизированные JVM параметры
ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZUncommitDelay=300 -XX:ZCollectionInterval=30 -Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]