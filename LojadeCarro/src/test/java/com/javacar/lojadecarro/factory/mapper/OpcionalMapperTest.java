package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.OpcionalResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.factory.opcional.OpcionalRequestFactory;
import com.javacar.lojadecarro.mapper.OpcionalMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarOpcionalEntity;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarOpcionalRequest;
import static org.assertj.core.api.Assertions.assertThat;

class OpcionalMapperTest extends MapperTest {
    @Autowired
    private OpcionalMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarOpcionalRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Opcional::getNome,
                        Opcional::isAtivo
                )
                .containsExactly(
                        "Freio ABS",
                        true
                );
    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarOpcionalEntity();

        var response = mapper.toResponse(entity);

        assertThat(response)
                .extracting(
                        OpcionalResponse::id,
                        OpcionalResponse::nome,
                        OpcionalResponse::ativo
                ).containsExactly(
                        1L,
                        "Freio ABS",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = OpcionalRequestFactory
                .criarRequest()
                .comNome("Banco de couro")
                .build();

        var entity = criarOpcionalEntity();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .extracting(
                        Opcional::getId,
                        Opcional::getNome,
                        Opcional::isAtivo
                ).containsExactly(
                        entity.getId(),
                        request.nome(),
                        entity.isAtivo()
                );
    }
}
