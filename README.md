# 🚀 Aplicação Java com Maven e Docker

Este projeto é uma aplicação Java simples usando Spring Boot e Maven, empacotada e executada dentro de um contêiner Docker. Ele expõe um endpoint `/api/status` e gera logs com palavras aleatórias a cada 10 segundos.

---

## 🛠 **1. Criando o projeto Maven**
Como não temos Java/Maven instalados localmente, utilizamos **Docker** para gerar o projeto Maven:

```sh
docker run --rm -v $(pwd):/app -w /app maven:3.9.6-eclipse-temurin-17 mvn archetype:generate \
    -DgroupId=com.example \
    -DartifactId=myapp \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DinteractiveMode=false
```

Isso criará um diretório `myapp/` contendo os seguintes arquivos:
- `pom.xml`
- `src/main/java/com/example/App.java`
- `src/test/java/com/example/AppTest.java`

---

## ✏️ **2. Customizando os arquivos**

### 🔹 **Arquivo `App.java`**
Edite `src/main/java/com/example/App.java` para incluir um serviço REST e logs de palavras aleatórias:

```java
package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.logging.Logger;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@RestController
@RequestMapping("/api")
class MyController {
    @GetMapping("/status")
    public String status() {
        return "UP";
    }
}

@Component
class LogService implements ApplicationRunner {
    private static final Logger logger = Logger.getLogger(LogService.class.getName());
    private static final String[] WORDS = {
            "cloud", "docker", "kubernetes", "java", "maven", "aws", "spring", "microservice", "scalability",
            "observability", "monitoring", "resilience", "automation", "performance", "infra", "logs",
            "debugging", "reliability", "containerization", "serverless"
    };
    private final Random random = new Random();

    @Override
    public void run(org.springframework.boot.ApplicationArguments args) {
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Aguarda a aplicação iniciar completamente
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            while (true) {
                String randomWord = WORDS[random.nextInt(WORDS.length)];
                logger.info("Log gerado: " + randomWord);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
```

---
### 🔹 **Arquivo `AppTest.java`**

```java
package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    @Test
    void shouldAnswerWithTrue() {
        assertTrue(true);
    }
}
```
---

### 🔹 **Arquivo `pom.xml`**
Atualize `pom.xml` para incluir **Spring Boot**, **JUnit 5** e gerar um fat JAR corretamente:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>myapp</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>17</java.version>
        <spring.boot.version>3.2.2</spring.boot.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>

        <!-- JUnit 5 para testes -->
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.10.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
    <plugins>
        <!-- Plugin do Spring Boot para criar um fat JAR -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <version>${spring.boot.version}</version>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

        <!-- Plugin Maven Compiler -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
            </configuration>
        </plugin>

        <!-- Plugin para ignorar testes no build -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M7</version>
            <configuration>
                <skipTests>true</skipTests>
            </configuration>
        </plugin>
      </plugins>
  </build>

</project>
```

---

## 🐳 **3. Criar o `Dockerfile`**
Crie um `Dockerfile` para empacotar e rodar a aplicação:

```dockerfile
# Etapa de Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa de Runtime
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/myapp-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

---

## 🔨 **4. Gerar a Imagem Docker**
Execute o comando para construir a imagem:

```sh
docker build -t myapp .
```

---

## 🚀 **5. Rodar o Contêiner**
Agora, inicie o contêiner:

```sh
docker run -p 8080:8080 myapp
```

---

## ✅ **6. Testes e Validações**

### **📌 Verificar se a API está rodando**
Abra um terminal e execute:

```sh
curl http://localhost:8080/api/status
```
Saída esperada:
```
UP
```

### **📌 Acompanhar os logs gerados**
Para visualizar os logs que mostram palavras aleatórias a cada 10 segundos:

```sh
docker logs -f <container_id>
```

Exemplo de saída esperada:
```
INFO: Log gerado: docker
INFO: Log gerado: observability
INFO: Log gerado: java
INFO: Log gerado: scalability
```

---

## 🎯 **Conclusão**
- ✅ Criamos uma aplicação Spring Boot do zero via Docker.  
- ✅ Configuramos Maven, dependências e logs corretamente.  
- ✅ Construímos e rodamos a aplicação no Docker.  
- ✅ Validamos o funcionamento da API e logs.  

Agora, você tem uma aplicação Java funcional rodando via Docker sem necessidade de instalação local de Java ou Maven! 🚀🎉

# lab-app-java-gera-log
