package com.javacar.lojadecarro.factory.carro;

import com.javacar.lojadecarro.entity.Carro;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class CarroEntityFactory {

    private final Carro carro;

    private CarroEntityFactory() {
        this.carro = new Carro();
    }

    public static CarroEntityFactory criarEntity() {
        return new CarroEntityFactory();
    }

    public CarroEntityFactory comTodosOsCampos() {
        carro.setId(1L);
        carro.setQuilometragem(67000.98);
        carro.setUrl("https://bucket/imagens/onix.jpg");
        carro.setValor(new BigDecimal(58000));
        carro.setPlaca("QUV1F836");
        carro.setMotor("1.0");
        carro.setAnoFabricacao(2020);
        carro.setDtCadastro(LocalDateTime.now());
        carro.setAtivo(true);

        return this;
    }


    public CarroEntityFactory comNome(String placa) {
        carro.setPlaca(placa);
        return this;
    }

    public CarroEntityFactory comId(Long id) {
        carro.setId(id);
        return this;
    }
    public CarroEntityFactory comPlaca(String placa) {
        carro.setPlaca(placa);
        return this;
    }

    public Carro build() {
        return carro;
    }
}
