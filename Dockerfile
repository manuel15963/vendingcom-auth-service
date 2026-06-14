# Imagen base con Java 17
FROM eclipse-temurin:17-jdk
# Carpeta interna del contenedor
WORKDIR /app

# Copiar el JAR generado por Maven
COPY target/*.jar app.jar

# Puerto donde corre el microservicio
EXPOSE 8081

# Comando para levantar la app
ENTRYPOINT ["java", "-jar", "app.jar"]