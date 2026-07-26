INSERT INTO usuario (
    cpf,
    data_nascimento,
    email,
    nome,
    password_hash,
    ativo,
    data_cadastro
)
VALUES (
           '15152736799',
           STR_TO_DATE('14/05/1991', '%d/%m/%Y'),
           'felipesmacario@gmail.com',
           'Felipe',
           '123',
           b'1',
           NOW(6)
       );