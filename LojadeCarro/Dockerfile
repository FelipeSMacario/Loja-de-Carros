FROM mysql:8.4

# Define senha do root e banco inicial
ENV MYSQL_ROOT_PASSWORD=root123
ENV MYSQL_DATABASE=loja_de_carros

# Copia script de inicialização para dentro do container
COPY init.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
