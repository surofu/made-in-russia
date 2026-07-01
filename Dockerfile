ARG ALPINE_VERSION=3.23

FROM eclipse-temurin:25-jdk-alpine AS java-builder
WORKDIR /build

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw dependency:go-offline -B

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw clean package -DskipTests -B

RUN java -Djarmode=layertools -jar target/*.jar extract --destination extracted

RUN set -eux; \
    CP="extracted/dependencies/BOOT-INF/lib/*"; \
    if [ -d extracted/snapshot-dependencies/BOOT-INF/lib ]; then \
        CP="${CP}:extracted/snapshot-dependencies/BOOT-INF/lib/*"; \
    fi; \
    DETECTED=$(jdeps -q \
        --multi-release 25 \
        --ignore-missing-deps \
        --print-module-deps \
        --class-path "${CP}" \
        extracted/application/BOOT-INF/classes); \
    echo "${DETECTED},jdk.crypto.ec,jdk.unsupported,java.xml,java.logging,java.desktop" \
        | tr -d ' ' > /tmp/modules.txt

RUN jlink \
    --add-modules "$(cat /tmp/modules.txt)" \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=zip-9 \
    --output /custom-jre

FROM alpine:${ALPINE_VERSION} AS runtime
WORKDIR /app

RUN apk add --no-cache \
        --repository=https://dl-cdn.alpinelinux.org/alpine/edge/testing \
        catdoc \
    && apk add --no-cache libwebp-tools \
    && addgroup -S spring && adduser -S spring -G spring

ENV JAVA_HOME=/opt/java/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=java-builder /custom-jre $JAVA_HOME

COPY --from=java-builder --chown=spring:spring /build/extracted/dependencies/ ./
COPY --from=java-builder --chown=spring:spring /build/extracted/spring-boot-loader/ ./
COPY --from=java-builder --chown=spring:spring /build/extracted/snapshot-dependencies/ ./
COPY --from=java-builder --chown=spring:spring /build/extracted/application/ ./

USER spring:spring

ENV JAVA_OPTS="-XX:+UseCompactObjectHeaders \
    -XX:+UseG1GC \
    -XX:+UseStringDeduplication \
    -XX:MaxRAMPercentage=75 \
    -XX:+ExitOnOutOfMemoryError \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/tmp/heapdump.hprof"

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} \
    --sun-misc-unsafe-memory-access=allow \
    --enable-native-access=ALL-UNNAMED \
    org.springframework.boot.loader.launch.JarLauncher"]