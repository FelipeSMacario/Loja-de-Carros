Loja de Carros — Frontend Angular
Frontend de uma aplicação para anúncio e comercialização de veículos, desenvolvido com Angular e integrado a uma API Java com Spring Boot.
Este é um projeto pessoal de portfólio, em desenvolvimento, utilizado para aprofundar conhecimentos em desenvolvimento full-stack e integração entre frontend e backend.
Funcionalidades
- Pesquisa e listagem de veículos.
- Aplicação de filtros na pesquisa.
- Visualização dos detalhes dos anúncios.
- Galeria de imagens dos veículos.
- Consumo da API REST do backend.
Tecnologias
- Angular — projeto gerado com Angular CLI 12.1.0.
- TypeScript.
- HTML e CSS.
- Integração com API REST.
Organização do repositório
O projeto completo é organizado em um monorepositório:
Diretório	Conteúdo
LojaDeCarroAngular/	Frontend Angular
LojadeCarro/	Backend Java com Spring Boot


Este README descreve o frontend. A configuração e a execução da API devem ser consultadas na documentação do backend.
Executar localmente
Pré-requisitos
- Git.
- Node.js e npm em versões compatíveis com a versão do Angular utilizada no projeto. Consulte as dependências em package.json antes de configurar o ambiente.
- Backend em execução para utilizar as funcionalidades que dependem da API.
Instalar as dependências
Na raiz do repositório, acesse o diretório do frontend:
cd LojaDeCarroAngular
npm install
Configurar a integração com o backend
Confira a URL da API na configuração utilizada pelo frontend e ajuste-a para o seu ambiente, se necessário. O backend deve permitir requisições da origem do frontend por meio da configuração de CORS.
Iniciar o servidor de desenvolvimento
Dentro do diretório do frontend, execute:
npx ng serve
Acesse http://localhost:4200/. A aplicação é recarregada automaticamente quando os arquivos de origem são alterados.
Build
Para gerar o build do frontend:
npx ng build
Os arquivos gerados ficam em dist/, conforme a configuração de saída do projeto.
Testes
Para executar os testes unitários configurados com Karma:
npx ng test
Contexto do projeto completo
O backend utiliza Java, Spring Boot, Spring Security com JWT, JPA/Hibernate e MySQL. Também inclui migrações com Flyway, documentação com Swagger/OpenAPI, testes unitários e de integração e integração contínua com GitHub Actions.
Esses recursos pertencem ao backend; os comandos deste README se referem ao frontend Angular.
Status
Projeto em desenvolvimento, com evolução das funcionalidades e da interface.
