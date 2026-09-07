ALTER TABLE usuario
    ADD COLUMN identity_provider_id VARCHAR(255);

ALTER TABLE usuario
    ADD CONSTRAINT uk_usuario_identity_provider_id
        UNIQUE (identity_provider_id);