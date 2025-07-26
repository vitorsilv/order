# Dockerfile
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copia o arquivo de build e baixa as dependências
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Instala o Maven
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copia o código fonte
COPY src src

# Constrói a aplicação
RUN ./mvnw clean package -DskipTests

# Remove o JAR original e renomeia para padrão
RUN find /app/target -name '*.jar' -exec mv {} /app/app.jar \;

# Expõe a porta da aplicação
EXPOSE 8080

# Comando de execução
ENTRYPOINT ["java", "-jar", "app.jar"]