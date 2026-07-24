package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoEntity;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoRequest;
import static org.assertj.core.api.Assertions.assertThat;

class VeiculoMapperTest extends MapperTest {
    @Autowired
    private VeiculoMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarVeiculoRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Veiculo::getQuilometragem,
                        Veiculo::getValor,
                        Veiculo::getPlaca,
                        Veiculo::getMotor,
                        Veiculo::getDescricao,
                        Veiculo::getAnoFabricacao

                )
                .containsExactly(
                        67000,
                        new BigDecimal(58000),
                        "QUV1F836",
                        "1.0",
                        "Documentos em dia",
                        (short) 2020
                );

    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarVeiculoEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        VeiculoResponse::id,
                        VeiculoResponse::placa,
                        VeiculoResponse::marca,
                        VeiculoResponse::modelo,
                        VeiculoResponse::valor,
                        VeiculoResponse::quilometragem,
                        VeiculoResponse::anoFabricacao,
                        VeiculoResponse::statusVeiculo
                )
                .containsExactly(
                        1L,
                        "QUV1F836",
                        "Ford",
                        "Onix",
                        new BigDecimal(58000),
                        67000,
                        (short) 2020,
                        DISPONIVEL
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = VeiculoRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comDescricao("Veiculo de leilão")
                .comMotor("1.3")
                .build();

        var entity = criarVeiculoEntity();
        var modeloEntity = entity.getModelo();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Veiculo::getId,
                        Veiculo::getAnoFabricacao,
                        Veiculo::getMotor,
                        Veiculo::getDescricao,
                        Veiculo::getPlaca,
                        Veiculo::getQuilometragem,
                        Veiculo::getValor,
                        Veiculo::getDataCadastro,
                        Veiculo::getStatusVeiculo)
                .containsExactly(
                        entity.getId(),
                        entity.getAnoFabricacao(),
                        request.motor(),
                        request.descricao(),
                        entity.getPlaca(),
                        entity.getQuilometragem(),
                        entity.getValor(),
                        entity.getDataCadastro(),
                        entity.getStatusVeiculo()
                );

        assertThat(modeloEntity).isSameAs(entity.getModelo());
    }
}
