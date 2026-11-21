# ---- Étape 1 : build Maven ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Cache Maven partagé entre les builds + retries réseau
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -q package -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=10 \
    -Dmaven.wagon.http.retryHandler.requestSentEnabled=true \
    -Dhttp.keepAlive=false

# ---- Étape 2 : image d'exécution légère ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
