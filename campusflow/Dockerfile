FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew.bat gradlew.bat
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

COPY src src

RUN chmod +x ./gradlew || true
RUN ./gradlew bootJar -x test

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

