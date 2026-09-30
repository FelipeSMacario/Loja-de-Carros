# Loja de Carros — Web

Interface web para pesquisa e visualização de anúncios de veículos, desenvolvida com Angular e integrada ao backend Java com Spring Boot do projeto Loja de Carros.

Projeto pessoal de portfólio em desenvolvimento, utilizado para aprofundar conhecimentos em desenvolvimento full-stack e integração com APIs REST.

## Funcionalidades

- Pesquisa e listagem de veículos.
- Filtros para consulta de anúncios.
- Visualização dos detalhes dos veículos.
- Galeria de imagens dos anúncios.
- Integração com a API REST do backend.

## Tecnologias

| Área | Tecnologia |
| --- | --- |
| Framework | Angular |
| Linguagem | TypeScript |
| Interface | HTML e CSS |
| Testes unitários | Vitest |
| Ferramentas | Angular CLI, Node.js e npm |

O projeto foi gerado com **Angular CLI 22.1.7**. As versões das dependências estão definidas no `package.json`.

## Executar localmente

### Pré-requisitos

- Node.js e npm compatíveis com as dependências do projeto.
- Backend em execução para as funcionalidades que consomem a API.

### Instalar as dependências

No diretório do projeto web, onde está o `package.json`, execute:

```bash
npm install
```

### Configurar a API

Confira o endereço do backend na configuração utilizada pela aplicação e ajuste-o para o seu ambiente, se necessário. A API deve permitir requisições da origem do frontend por meio da configuração de CORS.

### Iniciar o servidor de desenvolvimento

```bash
npx ng serve
```

Acesse **http://localhost:4200/**. A interface é recarregada automaticamente quando os arquivos de origem são alterados.

## Build

```bash
npx ng build
```

Os arquivos compilados são gerados em `dist/`, conforme a configuração de saída do projeto. O build padrão aplica as otimizações configuradas para produção.

## Testes unitários

Para executar os testes configurados com Vitest:

```bash
npx ng test
```

## Backend

O backend utiliza Java e Spring Boot, com autenticação JWT, persistência de dados e gerenciamento dos anúncios e imagens. Consulte o README do backend para configurar e executar a API.

## Status

Projeto em desenvolvimento, com evolução das funcionalidades e da interface.
