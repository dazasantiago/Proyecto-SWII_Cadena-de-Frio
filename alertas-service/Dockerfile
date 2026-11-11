# ---------- Etapa 1: compilar ----------
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

# Primero solo el pom: la capa de dependencias queda en caché mientras el pom no cambie
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Etapa 2: ejecutar ----------
# Solo el JRE y el .jar: sin Maven, sin JDK, sin código fuente
FROM eclipse-temurin:25-jre
WORKDIR /app

# No se ejecuta como root dentro del contenedor
RUN useradd --system --no-create-home alertas
USER alertas

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
