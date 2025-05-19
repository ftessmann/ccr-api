FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn -B -DskipTests clean package

RUN ls -la target/

CMD ["java", "-jar", "target/ccr-api-dev.jar"]
