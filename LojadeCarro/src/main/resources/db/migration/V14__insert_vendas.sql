INSERT INTO venda (
    status_venda,
    valor_venda,
    veiculo_id,
    comprador_id,
    vendedor_id
) VALUES
-- Venda concluída
('CONCLUIDA', 58990.00, 1, 3, 2),

-- Venda concluída
('CONCLUIDA', 129900.00, 2, 4, 2),

-- Venda cancelada
('CANCELADA', 175000.00, 3, 5, 2),

-- Venda concluída
('CONCLUIDA', 84990.00, 4, 6, 2),

-- Venda em andamento
('EM_ANDAMENTO', 69990.00, 5, 7, 2);