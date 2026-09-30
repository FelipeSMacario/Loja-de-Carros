# Loja de Carros — Backend

API REST para anúncio e comercialização de veículos, desenvolvida em Java com Spring Boot. Integra um projeto pessoal de portfólio full-stack, com interface web em Angular.

## Funcionalidades

- Cadastro e gerenciamento de veículos e informações relacionadas, como marcas, modelos, cores, combustíveis, carrocerias e opcionais.
- Pesquisa de veículos e consulta de anúncios.
- Autenticação com JWT e autorização por perfis de acesso.
- Regras de acesso para operações vinculadas ao vendedor do veículo.
- Upload e gerenciamento de imagens dos anúncios.
- Gerenciamento de vendas.
- Validação de dados e tratamento centralizado de erros.

## Tecnologias

| Área | Tecnologias |
| --- | --- |
| Linguagem e framework | Java, Spring Boot |
| API e validação | Spring Web, Bean Validation |
| Segurança | Spring Security, JWT |
| Persistência | Spring Data JPA, Hibernate, MySQL |
| Migrações | Flyway |
| Mapeamento e código auxiliar | MapStruct, Lombok |
| Documentação da API | Swagger/OpenAPI |
| Testes | JUnit, Mockito, AssertJ, Testcontainers |
| Cobertura e qualidade | JaCoCo, SonarCloud |
| Build e integração contínua | Maven, GitHub Actions |

As versões das dependências estão definidas no `pom.xml`.

## Organização da aplicação

A aplicação utiliza controllers para expor os endpoints, services para implementar as regras de negócio e repositories para acessar os dados. DTOs representam as entradas e saídas da API, com mapeamento realizado por MapStruct.

O tratamento centralizado de exceções padroniza as respostas de erro. As migrações do Flyway versionam a estrutura do banco de dados.

## Executar localmente

### Pré-requisitos

- JDK compatível com a versão definida no `pom.xml`.
- MySQL configurado para o ambiente de execução.
- Docker disponível para os testes de integração que utilizam Testcontainers.

### Configuração

Antes de iniciar a aplicação, confira as configurações em `src/main/resources` e no perfil utilizado:

- URL, usuário e senha do banco de dados.
- Porta e eventual caminho de contexto da aplicação.
- Origem permitida pelo CORS para o frontend.
- Configurações de armazenamento de imagens.

Forneça credenciais pelo mecanismo de configuração adotado no ambiente e evite incluí-las no repositório.

### Iniciar a API

Execute os comandos no diretório que contém o `pom.xml` e o Maven Wrapper.

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

O frontend deve apontar para o endereço e a porta configurados para esta API.

## Documentação da API

A documentação dos endpoints utiliza Swagger/OpenAPI. Com a aplicação em execução, consulte o Swagger UI no caminho configurado pelo projeto.

Para testar operações protegidas, obtenha um token pelo endpoint de login e envie-o no cabeçalho:

```http
Authorization: Bearer <token>
```

## Testes e build

Para executar os testes unitários e de integração e as verificações configuradas no Maven:

Linux/macOS:

```bash
./mvnw clean verify
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean verify
```

Os testes de integração com Testcontainers precisam de Docker em execução. O projeto utiliza JaCoCo para análise de cobertura, SonarCloud para análise de qualidade e GitHub Actions para integração contínua.

## Frontend

A interface web é desenvolvida no projeto **LojaDeCarrosWeb**, com Angular. Consulte o README do frontend para configurar e executar a interface.

## Status

Projeto pessoal de portfólio em desenvolvimento, com evolução das funcionalidades, testes e integrações.
