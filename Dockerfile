FROM eclipse-temurin:17-jdk AS build

WORKDIR /workspace

COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

RUN chmod +x ./gradlew

COPY src ./src

RUN ./gradlew --no-daemon bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=build /workspace/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "java ${JAVA_OPTS:-} -Dserver.port=${PORT:-8080} -jar app.jar"]
