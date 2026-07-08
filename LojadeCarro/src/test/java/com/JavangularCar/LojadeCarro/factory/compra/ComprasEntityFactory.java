package com.JavangularCar.LojadeCarro.factory.compra;

import com.JavangularCar.LojadeCarro.entity.Compras;
import com.JavangularCar.LojadeCarro.factory.carro.CarroEntityFactory;
import com.JavangularCar.LojadeCarro.factory.usuario.UsuarioEntityFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class ComprasEntityFactory {

    private final Compras compras;

    private ComprasEntityFactory() {
        this.compras = new Compras();
    }

    public static ComprasEntityFactory criarEntity() {
        return new ComprasEntityFactory();
    }

    public ComprasEntityFactory comTodosOsCampos() {
        compras.setId(1L);
        compras.setCarro(CarroEntityFactory.criarEntity().comTodosOsCampos().build());
        compras.setVendedor(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        compras.setComprador(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        compras.setValor(BigDecimal.valueOf(200000));
        compras.setDataVenda(Instant.now());
        return this;
    }

    public ComprasEntityFactory comTodosOsCamposExcetoId() {
        compras.setCarro(CarroEntityFactory.criarEntity().comTodosOsCampos().build());
        compras.setVendedor(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        compras.setComprador(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        compras.setValor(BigDecimal.valueOf(200000));
        compras.setDataVenda(Instant.now());
        return this;
    }

    public ComprasEntityFactory comId(Long id) {
        compras.setId(id);
        return this;
    }


    public Compras build() {
        return compras;
    }
}
