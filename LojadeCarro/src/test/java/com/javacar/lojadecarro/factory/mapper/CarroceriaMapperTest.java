package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.CarroceriaResponse;
import com.javacar.lojadecarro.entity.Carroceria;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaEntityFactory;
import com.javacar.lojadecarro.factory.carroceria.CarroceriaRequestFactory;
import com.javacar.lojadecarro.mapper.CarroceriaMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.criarCarroceriaEntity;
import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.criarCarroceriaRequest;
import static org.assertj.core.api.Assertions.assertThat;

class CarroceriaMapperTest extends MapperTest {
    @Autowired
    private CarroceriaMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarCarroceriaRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Carroceria::getNome,
                        Carroceria::isAtivo
                )
                .containsExactly(
                        "Hatch",
                        true
                );
    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarCarroceriaEntity();

        var response = mapper.toResponse(entity);

        assertThat(response)
                .extracting(
                        CarroceriaResponse::id,
                        CarroceriaResponse::nome,
                        CarroceriaResponse::ativo
                ).containsExactly(
                        1L,
                        "Hatch",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = CarroceriaRequestFactory
                .criarRequest()
                .comNome("SUV")
                .build();

        var entity = CarroceriaEntityFactory
                .criarEntity()
                .comId(1L)
                .comNome("Hatch")
                .comAtivo(true)
                .build();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .extracting(
                        Carroceria::getId,
                        Carroceria::getNome,
                        Carroceria::isAtivo
                ).containsExactly(
                        entity.getId(),
                        request.nome(),
                        entity.isAtivo()
                );
    }
}
