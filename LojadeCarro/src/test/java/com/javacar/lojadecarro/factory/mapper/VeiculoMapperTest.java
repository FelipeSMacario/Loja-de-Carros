package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.*;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.VeiculoOpcional;
import com.javacar.lojadecarro.factory.imagem.ImagemEntityFactory;
import com.javacar.lojadecarro.factory.opcional.OpcionalEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.mapper.VeiculoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoEntity;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

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
                        "QUV1F83",
                        "1.0",
                        "Documentos em dia",
                        (short) 2020
                );

    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarVeiculoEntity();
        var response = mapper.toResponse(entity, 10L);

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
                        "QUV1F83",
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
    @Test
    @DisplayName("Deve converter a entidade para resposta detalhada")
    void deveConverterEntityParaDetalheResponse() {
        // Arrange
        var entity = criarVeiculoEntity();
        entity.setImagens(
                List.of(ImagemEntityFactory
                        .criarEntity()
                                .comTodosOsCampos()
                                .comPrincipal(true)
                        .build(),
                        ImagemEntityFactory
                                .criarEntity()
                                .comTodosOsCampos()
                                .comId(2L)
                                .comPrincipal(false)
                                .build()
                        )
        );
        var opcionais = List.of(
                OpcionalEntityFactory.criarEntity().comId(1L).comNome("Freio Abs").comAtivo(true).build(),
                OpcionalEntityFactory.criarEntity().comId(2L).comNome("Multimidia").comAtivo(true).build(),
                OpcionalEntityFactory.criarEntity().comId(3L).comNome("Roda liga leve").comAtivo(true).build()
        );
        entity.setOpcionais(
                List.of(
                        new VeiculoOpcional(entity, opcionais.getFirst()),
                        new VeiculoOpcional(entity, opcionais.get(1)),
                        new VeiculoOpcional(entity, opcionais.getLast())
                )
        );

        // Act
        var response = mapper.toDetalheResponse(entity);

        // Assert
        assertThat(response)
                .isNotNull()
                .extracting(
                        VeiculoDetalheResponse::id,
                        VeiculoDetalheResponse::marca,
                        VeiculoDetalheResponse::modelo,
                        VeiculoDetalheResponse::carroceria,
                        VeiculoDetalheResponse::cor,
                        VeiculoDetalheResponse::combustivel,
                        VeiculoDetalheResponse::motor,
                        VeiculoDetalheResponse::valor,
                        VeiculoDetalheResponse::quilometragem,
                        VeiculoDetalheResponse::anoFabricacao,
                        VeiculoDetalheResponse::statusVeiculo,
                        VeiculoDetalheResponse::descricao
                )
                .containsExactly(
                        entity.getId(),
                        entity.getModelo().getMarca().getNome(),
                        entity.getModelo().getNome(),
                        entity.getCarroceria().getNome(),
                        entity.getCor().getNome(),
                        entity.getCombustivel().getNome(),
                        entity.getMotor(),
                        entity.getValor(),
                        entity.getQuilometragem(),
                        entity.getAnoFabricacao(),
                        entity.getStatusVeiculo(),
                        entity.getDescricao()
                );

        assertThat(response.vendedor())
                .isNotNull()
                .extracting(
                        VendedorResumoResponse::id,
                        VendedorResumoResponse::nome
                )
                .containsExactly(
                        entity.getVendedor().getId(),
                        entity.getVendedor().getNome()
                );

        assertThat(response.imagens())
                .extracting(
                        VeiculoImagemResponse::id,
                        VeiculoImagemResponse::principal
                )
                .containsExactlyInAnyOrder(
                        tuple(1L, true),
                        tuple(2L, false)
                );

        assertThat(response.opcionais())
                .extracting(
                        OpcionalResponse::id,
                        OpcionalResponse::nome,
                        OpcionalResponse::ativo
                )
                .containsExactlyInAnyOrder(
                        tuple(1L, "Freio Abs", true),
                        tuple(2L, "Multimidia", true),
                        tuple(3L, "Roda liga leve", true)
                );
    }

    @Test
    @DisplayName("Deve converter a entidade para resposta de edição")
    void deveConverterEntityParaEdicaoResponse() {
        // Arrange
        var entity = criarVeiculoEntity();

        entity.setImagens(
                List.of(
                        ImagemEntityFactory
                                .criarEntity()
                                .comTodosOsCampos()
                                .comPrincipal(true)
                                .build(),
                        ImagemEntityFactory
                                .criarEntity()
                                .comTodosOsCampos()
                                .comId(2L)
                                .comPrincipal(false)
                                .build()
                )
        );

        var opcionais = List.of(
                OpcionalEntityFactory
                        .criarEntity()
                        .comId(1L)
                        .comNome("Freio ABS")
                        .comAtivo(true)
                        .build(),
                OpcionalEntityFactory
                        .criarEntity()
                        .comId(2L)
                        .comNome("Multimídia")
                        .comAtivo(true)
                        .build()
        );

        entity.setOpcionais(
                List.of(
                        new VeiculoOpcional(
                                entity,
                                opcionais.getFirst()
                        ),
                        new VeiculoOpcional(
                                entity,
                                opcionais.getLast()
                        )
                )
        );

        // Act
        var response =
                mapper.toEdicaoResponse(entity);

        // Assert
        assertThat(response)
                .isNotNull()
                .extracting(
                        VeiculoEdicaoResponse::id,
                        VeiculoEdicaoResponse::placa,
                        VeiculoEdicaoResponse::quilometragem,
                        VeiculoEdicaoResponse::valor,
                        VeiculoEdicaoResponse::motor,
                        VeiculoEdicaoResponse::descricao,
                        VeiculoEdicaoResponse::anoFabricacao,
                        VeiculoEdicaoResponse::idCarroceria,
                        VeiculoEdicaoResponse::idCor,
                        VeiculoEdicaoResponse::idModelo,
                        VeiculoEdicaoResponse::idCombustivel,
                        VeiculoEdicaoResponse::statusVeiculo
                )
                .containsExactly(
                        entity.getId(),
                        entity.getPlaca(),
                        entity.getQuilometragem(),
                        entity.getValor(),
                        entity.getMotor(),
                        entity.getDescricao(),
                        entity.getAnoFabricacao(),
                        entity.getCarroceria().getId(),
                        entity.getCor().getId(),
                        entity.getModelo().getId(),
                        entity.getCombustivel().getId(),
                        entity.getStatusVeiculo()
                );

        assertThat(response.idsOpcionais())
                .containsExactly(1L, 2L);

        assertThat(response.imagens())
                .extracting(
                        VeiculoImagemResponse::id,
                        VeiculoImagemResponse::principal
                )
                .containsExactlyInAnyOrder(
                        tuple(1L, true),
                        tuple(2L, false)
                );
    }
}
