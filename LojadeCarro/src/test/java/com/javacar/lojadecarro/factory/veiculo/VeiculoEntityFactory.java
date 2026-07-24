package com.javacar.lojadecarro.factory.veiculo;

import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.ModeloHelper.criarModeloEntity;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class VeiculoEntityFactory {

    private final Veiculo veiculo;

    private VeiculoEntityFactory() {
        this.veiculo = new Veiculo();
    }

    public static VeiculoEntityFactory criarEntity() {
        return new VeiculoEntityFactory();
    }

    public VeiculoEntityFactory comTodosOsCampos() {
        veiculo.setId(1L);
        veiculo.setAnoFabricacao((short) 2020);
        veiculo.setMotor("1.0");
        veiculo.setDescricao("Documentos em dia");
        veiculo.setPlaca("QUV1F836");
        veiculo.setModelo(criarModeloEntity());
        veiculo.setQuilometragem(67000);
        veiculo.setValor(new BigDecimal(58000));
        veiculo.setDataCadastro(LocalDateTime.now());
        veiculo.setStatusVeiculo(DISPONIVEL);
        return this;
    }

    public VeiculoEntityFactory comId(Long id) {
        veiculo.setId(id);
        return this;
    }

    public VeiculoEntityFactory comStatus(StatusVeiculo statusVeiculo) {
        veiculo.setStatusVeiculo(statusVeiculo);
        return this;
    }


    public Veiculo build() {
        return veiculo;
    }
}
