FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

COPY . .

RUN mvn -B -DskipTests clean package

RUN apt-get update && apt-get install -y openssl
RUN mkdir -p /app/src/main/resources/

RUN openssl genrsa -out /app/src/main/resources/privateKey.pem 2048 && \
    openssl pkcs8 -topk8 -nocrypt -inform PEM -in /app/src/main/resources/privateKey.pem -out /app/src/main/resources/privateKey.pem && \
    openssl rsa -in /app/src/main/resources/privateKey.pem -pubout -outform PEM -out /app/src/main/resources/publicKey.pem


RUN ls -la /app/src/main/resources

RUN cat /app/src/main/resources/publicKey.pem

RUN ls -la target/

CMD ["java", "-jar", "target/quarkus-app/quarkus-run.jar"]
