package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.CorResponse;
import com.javacar.lojadecarro.entity.Cor;
import com.javacar.lojadecarro.factory.cor.CorRequestFactory;
import com.javacar.lojadecarro.mapper.CorMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.CorHelper.criarCorEntity;
import static com.javacar.lojadecarro.factory.helper.CorHelper.criarCorRequest;
import static org.assertj.core.api.Assertions.assertThat;

class CorMapperTest extends MapperTest {
    @Autowired
    private CorMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarCorRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Cor::getNome,
                        Cor::isAtivo
                )
                .containsExactly(
                        "Branco",
                        true
                );

    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarCorEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        CorResponse::id,
                        CorResponse::nome,
                        CorResponse::ativo
                )
                .containsExactly(
                        1L,
                        "Branco",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = CorRequestFactory
                .criarRequest()
                .comNome("Vermelho")
                .build();

        var entity = criarCorEntity();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Cor::getId,
                        Cor::getNome,
                        Cor::isAtivo)
                .containsExactly(
                        entity.getId(),
                        request.nome(),
                        entity.isAtivo()
                );
    }
}
