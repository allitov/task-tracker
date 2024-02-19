FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build
COPY src ./src
COPY pom.xml .
COPY mvnw .
COPY .mvn ./.mvn
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
ARG JAR_FILE=/build/target/*.jar
COPY --from=build $JAR_FILE ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]