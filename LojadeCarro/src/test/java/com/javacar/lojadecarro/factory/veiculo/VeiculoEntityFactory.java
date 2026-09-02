package com.javacar.lojadecarro.factory.veiculo;

import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
import com.javacar.lojadecarro.factory.combustivel.CombustivelEntityFactory;
import com.javacar.lojadecarro.factory.cor.CorEntityFactory;
import com.javacar.lojadecarro.factory.modelo.ModeloEntityFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
        veiculo.setPlaca("QUV1F83");
        veiculo.setModelo(criarModeloEntity());
        veiculo.setQuilometragem(67000);
        veiculo.setValor(new BigDecimal(58000));
        veiculo.setDataCadastro(LocalDateTime.now());
        veiculo.setVendedor(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        veiculo.setCarroceria(CarroceriaEntityFactory.criarEntity().comTodosOsCampos().build());
        veiculo.setCor(CorEntityFactory.criarEntity().comTodosOsCampos().build());
        veiculo.setModelo(ModeloEntityFactory.criarEntity().comTodosOsCampos().build());
        veiculo.setCombustivel(CombustivelEntityFactory.criarEntity().comTodosOsCampos().build());
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
    public VeiculoEntityFactory comImagens (List<Imagem> imagens) {
        veiculo.setImagens(imagens);
        return this;
    }

    public Veiculo build() {
        return veiculo;
    }
}
