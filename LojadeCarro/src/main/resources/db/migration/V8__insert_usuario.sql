INSERT INTO usuario (
    cpf,
    data_nascimento,
    email,
    nome,
    password_hash,
    ativo,
    data_cadastro
)
VALUES
    (
        '15052036000',
        STR_TO_DATE('14/05/1991', '%d/%m/%Y'),
        'felipe.vendedor@gmail.com',
        'Felipe Vendedor',
        '$2a$10$Um/b7z9VnOEzG8QzSQ.pVOEkDsSbgOQ5DlcDxvuSsOXhmrF6fbMG6',
        b'1',
        NOW(6)
    ),
    (
        '28473956100',
        STR_TO_DATE('22/08/1988', '%d/%m/%Y'),
        'joao.silva@gmail.com',
        'João Silva',
        '$2b$10$58Xkzl5zDmRVQ8LToevWfO4GDODH0d7oPdhvXBW.KXJl8YVFZYUoC',
        b'1',
        NOW(6)
    ),
    (
        '39584627111',
        STR_TO_DATE('10/01/1995', '%d/%m/%Y'),
        'maria.santos@gmail.com',
        'Maria Santos',
        '$2b$10$VKgOBxn1zDi2hyGWCmszY.jJejeno8pWAa3.b4E/Zjc2Tj9eL68li',
        b'1',
        NOW(6)
    ),
    (
        '47691835222',
        STR_TO_DATE('05/06/1990', '%d/%m/%Y'),
        'carlos.oliveira@gmail.com',
        'Carlos Oliveira',
        '$2b$10$UxHDvE/7gKzScyacj8EuI.xQGCSvCs0ypPN8w/Sa7Q9GxctjG94gS',
        b'1',
        NOW(6)
    ),
    (
        '44444444444',
        STR_TO_DATE('02/01/1982', '%d/%m/%Y'),
        'batmaimMorcegao@gmail.com',
        'Batman',
        '$2b$10$CPKS0mFinE4FOyhRtQymYeoOT/FEI509zAai3Z4/0NL0wng.lWxrK',
        b'0',
        NOW(6)
    ),
    (
        '66666666666',
        STR_TO_DATE('02/01/1982', '%d/%m/%Y'),
        'robin@gmail.com',
        'Robin',
        '$2b$10$rXCZl2RabdcEHs7tkCPKPu4Vf5mVONJQrntV0AosQZObZSXlWKPgq',
        b'0',
        NOW(6)
    ),
    (
        '11111112344',
        STR_TO_DATE('02/01/1982', '%d/%m/%Y'),
        'german@gmail.com',
        'German cano',
        '$2b$10$owkEv8nXCfiDUUMcd9JODOOuOSoTzM0HuhmFJp0PMp9Yp1SFA/Utm',
        b'1',
        NOW(6)
    );