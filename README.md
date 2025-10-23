# 🐶 Dog API Automation

[![CI/CD](https://github.com/danilopuh/dog-api-automation/actions/workflows/ci.yml/badge.svg)](https://github.com/danilopuh/dog-api-automation/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![RestAssured](https://img.shields.io/badge/RestAssured-5.5.0-green.svg)](https://rest-assured.io/)

Testes automatizados para a [Dog API](https://dog.ceo/dog-api/documentation) usando **Java 21**, **RestAssured**, **JUnit 5** e **Allure Reports**.

## 🎯 Endpoints Testados
- `GET /breeds/list/all` - Lista todas as raças
- `GET /breed/{breed}/images` - Imagens por raça específica  
- `GET /breeds/image/random` - Imagem aleatória

## 📦 Stack Tecnológica
- **Java 21** (LTS)
- **Maven 3.9+**
- **RestAssured 5.5.0**
- **JUnit 5.11.2** 
- **Allure 2.29.0**
- **Hamcrest**
- **JSON Schema Validator**
- **GitHub Actions**

## 🚀 Execução Local

### Pré-requisitos
- Java 21 (LTS)
- Maven 3.6+

### Comandos
```bash
# Executar todos os testes
mvn clean test

# Compilar apenas
mvn clean compile test-compile

# Gerar relatório Allure
mvn allure:report

# Servidor interativo do Allure
mvn allure:serve

# Build completo
mvn clean package
```

## 📊 Relatórios

### Surefire (JUnit)
- Localização: `target/surefire-reports/`
- Formato: XML/TXT

### Allure
- Resultados: `target/allure-results/`
- Relatório: `target/site/allure-maven-plugin/`
- **GitHub Pages**: Relatórios publicados automaticamente na branch `gh-pages`

## 🧪 Cenários de Teste

| Teste | Validações | Tempo Limite |
|-------|------------|--------------|
| **Lista de Raças** | Status 200, Schema JSON, Presença de raças | < 2s |
| **Imagens por Raça** | Status 200, Schema JSON, URLs válidas | < 3s |
| **Imagem Aleatória** | Status 200, Schema JSON, URL com extensão | < 2s |
| **Raça Inexistente** | Erro coerente (404 ou status=error) | - |
| **Performance** | Latência básica e contratos | < 2.5s |

## � Estrutura do Projeto
```
src/
├── test/
│   ├── java/br/com/dogapi/
│   │   ├── tests/DogApiTest.java      # Testes principais
│   │   └── utils/SchemaValidator.java # Validador de schema
│   └── resources/schemas/             # Schemas JSON
│       ├── list_all_schema.json
│       ├── breed_images_schema.json
│       └── random_image_schema.json
└── .github/workflows/ci.yml           # Pipeline CI/CD
```

## 🔄 Pipeline CI/CD

### Triggers
- Push para `main` e `develop`
- Pull Requests para `main`

### Jobs
1. **Tests** - Executa testes com Java 21
2. **Security Scan** - OWASP Dependency Check
3. **Deploy Reports** - Publica relatórios no GitHub Pages

### Artefatos Gerados
- Relatórios Surefire
- Resultados e relatórios Allure
- Relatório de segurança OWASP

## 📈 Features da Pipeline

- ✅ **Cache Maven** para builds mais rápidos
- ✅ **Múltiplas versões Java** (atualmente Java 21)
- ✅ **Relatórios automáticos** via GitHub Pages
- ✅ **Scan de segurança** com OWASP
- ✅ **Artefatos persistentes** (30 dias)

## 🛡️ Qualidade e Segurança

- **JSON Schema Validation** para contratos de API
- **OWASP Dependency Check** para vulnerabilidades
- **Test Coverage** via Allure
- **Performance Testing** com thresholds de tempo

## 💡 Notas Importantes

- Testes fazem chamadas **reais** à Dog API pública
- Thresholds de tempo são **conservadores** para reduzir flakiness
- Pipeline roda em **Ubuntu Latest** com Java 21
- Relatórios Allure disponíveis em **GitHub Pages**