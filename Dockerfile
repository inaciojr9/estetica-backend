# Etapa 1: Compilação da aplicação usando Maven e Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /build

# Copia os arquivos de configuração do Maven e dependências primeiro (otimiza o cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Copia o código fonte da aplicação
COPY src src

# Executa o empacotamento pulando os testes para acelerar o deploy na Render
RUN ./mvnw package -DskipTests

# Etapa 2: Container final de execução (Leve e seguro)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /work

# Copia o resultado do build (formato fast-jar padrão do Quarkus)
COPY --from=build /build/target/quarkus-app/ /work/

EXPOSE 8080

# Comando para iniciar o Quarkus escutando em todas as interfaces de rede na porta 8080
CMD ["java", "-jar", "/work/quarkus-run.jar", "-Dquarkus.http.host=0.0.0.0"]
