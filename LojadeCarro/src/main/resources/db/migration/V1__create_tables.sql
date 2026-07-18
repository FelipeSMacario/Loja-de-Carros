CREATE TABLE carroceria
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(30) NOT NULL,
    data_cadastro TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_carroceria PRIMARY KEY (id),
    CONSTRAINT uk_carroceria_nome UNIQUE (nome)
);

CREATE TABLE combustivel
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(30) NOT NULL,
    data_cadastro TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_combustivel PRIMARY KEY (id),
    CONSTRAINT uk_combustivel_nome UNIQUE (nome)
);

CREATE TABLE opcional
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(50) NOT NULL,
    data_cadastro TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_opcional PRIMARY KEY (id),
    CONSTRAINT uk_opcional_nome UNIQUE (nome)
);

CREATE TABLE cor
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(30) NOT NULL,
    data_cadastro TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_cor PRIMARY KEY (id),
    CONSTRAINT uk_cor_nome UNIQUE (nome)
);
CREATE TABLE marca
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(20)  NOT NULL,
    url           VARCHAR(100) NOT NULL,
    data_cadastro TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_marca PRIMARY KEY (id),
    CONSTRAINT uk_marca_nome
        UNIQUE (nome),
    CONSTRAINT uk_marca_url
        UNIQUE (url)
);
CREATE TABLE modelo
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(20) NOT NULL,
    marca_id      BIGINT      NOT NULL,
    data_cadastro TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_modelo PRIMARY KEY (id),

    CONSTRAINT fk_modelo_marca
        FOREIGN KEY (marca_id)
            REFERENCES marca (id),

    CONSTRAINT uk_modelo_id_marca_id
        UNIQUE (nome, marca_id)
);
CREATE TABLE usuario
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    cpf             VARCHAR(11)  NOT NULL,
    data_nascimento date         NOT NULL,
    email           VARCHAR(255) NOT NULL,
    nome            VARCHAR(150) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    data_cadastro   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo           BOOLEAN      NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT uk_usuario_cpf UNIQUE (cpf),
    CONSTRAINT uk_usuario_email UNIQUE (email)
);
CREATE TABLE role
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    nome          VARCHAR(50) NOT NULL,
    data_cadastro TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ativo         BOOLEAN     NOT NULL DEFAULT TRUE,

    CONSTRAINT pk_role PRIMARY KEY (id),
    CONSTRAINT uk_role_nome UNIQUE (nome)
);
CREATE TABLE usuario_role
(

    usuario_id BIGINT NOT NULL,
    role_id    BIGINT NOT NULL,

    CONSTRAINT pk_usuario_role
        PRIMARY KEY (usuario_id, role_id),

    CONSTRAINT fk_usuario_role_usuario
        FOREIGN KEY (usuario_id)
            REFERENCES usuario (id),

    CONSTRAINT fk_usuario_role_role
        FOREIGN KEY (role_id)
            REFERENCES role (id)
);
CREATE TABLE veiculo
(
    id             BIGINT AUTO_INCREMENT NOT NULL,

    ano_fabricacao SMALLINT       NOT NULL,
    motor          VARCHAR(30)    NOT NULL,
    placa          VARCHAR(7)     NOT NULL,
    quilometragem  INT            NOT NULL,
    valor          DECIMAL(12, 2) NOT NULL,
    descricao      TEXT,

    status_veiculo VARCHAR(20)    NOT NULL,

    data_cadastro  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    carroceria_id  BIGINT         NOT NULL,
    combustivel_id BIGINT         NOT NULL,
    cor_id         BIGINT         NOT NULL,
    modelo_id      BIGINT         NOT NULL,
    vendedor_id    BIGINT         NOT NULL,

    CONSTRAINT pk_veiculo
        PRIMARY KEY (id),

    CONSTRAINT fk_veiculo_carroceria
        FOREIGN KEY (carroceria_id)
            REFERENCES carroceria (id),

    CONSTRAINT fk_veiculo_combustivel
        FOREIGN KEY (combustivel_id)
            REFERENCES combustivel (id),

    CONSTRAINT fk_veiculo_cor
        FOREIGN KEY (cor_id)
            REFERENCES cor (id),

    CONSTRAINT fk_veiculo_modelo
        FOREIGN KEY (modelo_id)
            REFERENCES modelo (id),

    CONSTRAINT fk_veiculo_vendedor
        FOREIGN KEY (vendedor_id)
            REFERENCES usuario (id),

    CONSTRAINT uk_veiculo_placa
        UNIQUE (placa),

    CONSTRAINT fk_veiculo_status
        FOREIGN KEY (status_veiculo_id)
            REFERENCES status_veiculo (id)
);
CREATE TABLE imagem
(
    id            BIGINT AUTO_INCREMENT NOT NULL,

    nome_original VARCHAR(255) NOT NULL,
    object_key    VARCHAR(500) NOT NULL,
    bucket        VARCHAR(100) NOT NULL,

    content_type  VARCHAR(100) NOT NULL,
    tamanho       BIGINT       NOT NULL,

    principal     BOOLEAN      NOT NULL DEFAULT FALSE,

    veiculo_id    BIGINT       NOT NULL,

    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_imagem
        PRIMARY KEY (id),

    CONSTRAINT uk_imagem_bucket_object_key
        UNIQUE (bucket, object_key),

    CONSTRAINT fk_imagem_veiculo
        FOREIGN KEY (veiculo_id)
            REFERENCES veiculo (id)
);
CREATE TABLE venda
(
    id           BIGINT AUTO_INCREMENT NOT NULL,

    data_venda   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_venda VARCHAR(20)    NOT NULL,
    valor_venda  DECIMAL(12, 2) NOT NULL,

    veiculo_id   BIGINT         NOT NULL,

    comprador_id BIGINT         NOT NULL,

    vendedor_id  BIGINT         NOT NULL,

    CONSTRAINT pk_venda
        PRIMARY KEY (id),

    CONSTRAINT fk_venda_veiculo
        FOREIGN KEY (veiculo_id)
            REFERENCES veiculo (id),

    CONSTRAINT fk_venda_comprador
        FOREIGN KEY (comprador_id)
            REFERENCES usuario (id),
    CONSTRAINT fk_venda_vendedor
        FOREIGN KEY (vendedor_id)
            REFERENCES usuario (id),

    CONSTRAINT uk_venda_veiculo
        UNIQUE (veiculo_id)
);
CREATE TABLE veiculo_opcional
(

    veiculo_id  BIGINT NOT NULL,
    opcional_id BIGINT NOT NULL,


    CONSTRAINT pk_veiculo_opcional
        PRIMARY KEY (veiculo_id, opcional_id),

    CONSTRAINT fk_veiculo_opcional_veiculo
        FOREIGN KEY (veiculo_id)
            REFERENCES veiculo (id),

    CONSTRAINT fk_veiculo_opcional_opcional
        FOREIGN KEY (opcional_id)
            REFERENCES opcional (id)
);


CREATE INDEX idx_veiculo_opcional_veiculo
    ON veiculo_opcional (veiculo_id);

CREATE INDEX idx_veiculo_opcional_opcional
    ON veiculo_opcional (opcional_id);


CREATE INDEX idx_modelo_marca
    ON modelo (marca_id);

CREATE INDEX idx_imagem_veiculo_id
    ON imagem (veiculo_id);

CREATE INDEX idx_veiculo_modelo
    ON veiculo (modelo_id);

CREATE INDEX idx_veiculo_vendedor
    ON veiculo (vendedor_id);

CREATE INDEX idx_veiculo_carroceria
    ON veiculo (carroceria_id);

CREATE INDEX idx_veiculo_combustivel
    ON veiculo (combustivel_id);

CREATE INDEX idx_veiculo_cor
    ON veiculo (cor_id);

CREATE INDEX idx_veiculo_status
    ON veiculo (status_veiculo_id);

CREATE INDEX idx_veiculo_data_cadastro
    ON veiculo (data_cadastro);

CREATE INDEX idx_veiculo_valor
    ON veiculo (valor);

CREATE INDEX idx_veiculo_ano_fabricacao
    ON veiculo (ano_fabricacao);

CREATE INDEX idx_venda_comprador
    ON venda (comprador_id);

CREATE INDEX idx_venda_vendedor
    ON venda (vendedor_id);

CREATE INDEX idx_venda_data
    ON venda (data_venda);