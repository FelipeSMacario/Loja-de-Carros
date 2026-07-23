package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.MarcaResponse;
import com.javacar.lojadecarro.entity.Marca;
import com.javacar.lojadecarro.factory.marca.MarcaRequestFactory;
import com.javacar.lojadecarro.mapper.MarcaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.MarcaHelper.criarMarcaEntity;
import static com.javacar.lojadecarro.factory.helper.MarcaHelper.criarMarcaRequest;
import static org.assertj.core.api.Assertions.assertThat;

class MarcaMapperTest extends MapperTest {
    @Autowired
    private MarcaMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarMarcaRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Marca::getNome,
                        Marca::isAtivo,
                        Marca::getUrl
                ).containsExactly(
                        "Ford",
                        true,
                        "https://www.google.com"
                );
    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarMarcaEntity();

        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        MarcaResponse::id,
                        MarcaResponse::nome,
                        MarcaResponse::url,
                        MarcaResponse::ativo
                ).containsExactly(
                        1L,
                        "Ford",
                        "https://www.google.com",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = MarcaRequestFactory
                .criarRequest()
                .comNome("Fiat")
                .comUrl("https://www.youtube.com")
                .build();

        var entity = criarMarcaEntity();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Marca::getId,
                        Marca::getNome,
                        Marca::getUrl,
                        Marca::isAtivo
                ).containsExactly(
                        entity.getId(),
                        request.nome(),
                        request.url(),
                        entity.isAtivo()
                );
    }
}
