package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.mapper.ImagemMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.ImagemHelper.criarImagemEntity;
import static org.assertj.core.api.Assertions.assertThat;

class ImagemMapperTest extends MapperTest {
    @Autowired
    private ImagemMapper mapper;

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarImagemEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        ImagemResponse::id,
                        ImagemResponse::nomeOriginal,
                        ImagemResponse::objectKey,
                        ImagemResponse::principal
                )
                .containsExactly(
                        1L,
                        "nomeImagemOriginal",
                        "imagens/2026/foto.jpg",
                        true
                );
    }

}
