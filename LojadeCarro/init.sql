CREATE USER 'felipe'@'%' IDENTIFIED BY 'senha123';
GRANT ALL PRIVILEGES ON loja_de_carros.* TO 'felipe'@'%';
FLUSH PRIVILEGES;
