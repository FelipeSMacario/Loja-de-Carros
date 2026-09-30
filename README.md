# Loja de Carros

Aplicação full-stack para anúncio e comercialização de veículos, com backend em **Java e Spring Boot** e frontend em **Angular**.

Projeto pessoal de portfólio em desenvolvimento, voltado à prática de desenvolvimento de software, segurança, persistência, testes automatizados e integração contínua.

## Funcionalidades

- Pesquisa e listagem de veículos com filtros.
- Visualização de anúncios e galeria de imagens.
- Cadastro e gerenciamento de veículos e informações relacionadas.
- Autenticação com JWT e autorização por perfis de acesso.
- Regras de acesso vinculadas ao vendedor do veículo.
- Upload e gerenciamento de imagens.
- Gerenciamento de vendas.

As funcionalidades de negócio são implementadas no backend; a interface web está em evolução.

## Tecnologias

| Área | Tecnologias |
| --- | --- |
| Backend | Java, Spring Boot, Spring Web, Bean Validation |
| Segurança | Spring Security, JWT |
| Persistência | MySQL, Spring Data JPA, Hibernate, Flyway |
| Mapeamento e documentação | MapStruct, Lombok, Swagger/OpenAPI |
| Frontend | Angular, TypeScript, HTML, CSS |
| Testes do backend | JUnit, Mockito, AssertJ, Testcontainers |
| Testes do frontend web | Vitest |
| Qualidade | JaCoCo, SonarCloud |
| Build e integração contínua | Maven, npm, GitHub Actions |

As versões das dependências estão definidas no `pom.xml` do backend e no `package.json` do frontend. O projeto web atual foi gerado com Angular CLI 22.1.7.

## Organização

O repositório reúne o backend Java e o frontend **LojaDeCarrosWeb**. Cada módulo possui seu próprio README com instruções de configuração e execução.

O backend é organizado em controllers, services e repositories, com DTOs para entrada e saída, mapeamento com MapStruct e tratamento centralizado de exceções. O frontend consome os endpoints REST da API.

## Executar localmente

### Pré-requisitos

- JDK compatível com o `pom.xml` do backend.
- Node.js e npm compatíveis com as dependências do frontend.
- MySQL configurado para a aplicação.
- Docker em execução para os testes de integração com Testcontainers.

### Backend

Configure a conexão com o banco, o armazenamento de imagens e a origem permitida pelo CORS conforme as configurações da aplicação.

No diretório do backend, execute:

```bash
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

### Frontend web

No diretório do frontend, confira a configuração da URL da API e execute:

```bash
npm install
npx ng serve
```

Acesse **http://localhost:4200/**. O backend deve estar em execução para as funcionalidades que dependem da API.

## Testes e qualidade

No diretório do backend:

```bash
./mvnw clean verify
```

No Windows PowerShell:

```powershell
.\mvnw.cmd clean verify
```

Os testes de integração utilizam Testcontainers e precisam de Docker. A cobertura é analisada com JaCoCo e a qualidade do código com SonarCloud. O projeto utiliza GitHub Actions para integração contínua.

No diretório do frontend web:

```bash
npx ng test
```

## Documentação da API

O backend disponibiliza documentação com Swagger/OpenAPI. Consulte o README e a configuração do backend para acessar o Swagger UI e testar os endpoints.

## Status

Projeto em desenvolvimento, com evolução das funcionalidades, da interface e das integrações.
