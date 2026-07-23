package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.dto.response.ModeloResponse;
import com.javacar.lojadecarro.entity.Modelo;
import com.javacar.lojadecarro.factory.modelo.ModeloRequestFactory;
import com.javacar.lojadecarro.mapper.ModeloMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.ModeloHelper.criarModeloEntity;
import static com.javacar.lojadecarro.factory.helper.ModeloHelper.criarModeloRequest;
import static org.assertj.core.api.Assertions.assertThat;

class ModeloMapperTest extends MapperTest {

    @Autowired
    private ModeloMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarModeloRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Modelo::getNome,
                        Modelo::isAtivo
                )
                .containsExactly(
                        "Onix",
                        true
                );
    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarModeloEntity();

        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        ModeloResponse::id,
                        ModeloResponse::nome,
                        ModeloResponse::ativo
                ).containsExactly(
                        1L,
                        "Onix",
                        true
                );

        assertThat(response.marcaResponse())
                .isNotNull()
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::url,
                        MarcaResponse::ativo
                )
                .containsExactly(
                        1L,
                        "Ford",
                        "https://www.google.com",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = ModeloRequestFactory
                .criarRequest()
                .comNome("Argo")
                .build();

        var entity = criarModeloEntity();
        var marcaAntes = entity.getMarca();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .extracting(
                        Modelo::getId,
                        Modelo::getNome,
                        Modelo::isAtivo
                ).containsExactly(
                        entity.getId(),
                        request.nome(),
                        entity.isAtivo()
                );
        assertThat(entity.getMarca())
                .isSameAs(marcaAntes);
    }
}
