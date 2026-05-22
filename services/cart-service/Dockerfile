FROM gradle:8.10.0-jdk21 AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

COPY src src

FROM build AS test
RUN ./gradlew test --tests "*Test" -x "*IntegrationTest" --no-daemon

FROM build AS package
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=package /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]