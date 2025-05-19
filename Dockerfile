FROM maven:3.9.6-eclipse-temurin-21

WORKDIR /app

# Copie primeiro apenas os arquivos necessários para resolver dependências
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd ./
COPY pom.xml ./

# Dê permissão de execução ao mvnw
RUN chmod +x ./mvnw

# Para projetos desenvolvidos no Windows e executados no Linux
# Instale e use dos2unix para converter terminações de linha
RUN apt-get update && apt-get install -y dos2unix && dos2unix mvnw

# Agora execute o comando Maven
RUN ./mvnw -DoutputFile=target/mvn-dependency-list.log -B -DskipTests clean dependency:list install

# Copie o resto do código fonte
COPY src ./src/

# Comando para executar a aplicação
CMD ["java", "-jar", "target/*.jar"]
