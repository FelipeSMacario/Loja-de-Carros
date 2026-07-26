package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import com.javacar.lojadecarro.factory.combustivel.CombustivelRequestFactory;
import com.javacar.lojadecarro.mapper.CombustivelMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.CombustivelHelper.criarCombustivelEntity;
import static com.javacar.lojadecarro.factory.helper.CombustivelHelper.criarCombustivelRequest;
import static org.assertj.core.api.Assertions.assertThat;

class CombustivelMapperTest extends MapperTest {
    @Autowired
    private CombustivelMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarCombustivelRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Combustivel::getNome,
                        Combustivel::isAtivo
                )
                .containsExactly(
                        "Gasolina",
                        true
                );
    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarCombustivelEntity();

        var response = mapper.toResponse(entity);

        assertThat(response)
                .extracting(
                        CombustivelResponse::id,
                        CombustivelResponse::nome,
                        CombustivelResponse::ativo
                ).containsExactly(
                        1L,
                        "Gasolina",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = CombustivelRequestFactory
                .criarRequest()
                .comNome("Etanol")
                .build();

        var entity = criarCombustivelEntity();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .extracting(
                        Combustivel::getId,
                        Combustivel::getNome,
                        Combustivel::isAtivo
                ).containsExactly(
                        entity.getId(),
                        request.nome(),
                        entity.isAtivo()
                );
    }
}
