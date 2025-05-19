FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn -B -DskipTests clean package

RUN apt-get update && apt-get install -y openssl

RUN ls -la target/

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
