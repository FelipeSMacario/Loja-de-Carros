package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.mapper.VendasMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.factory.helper.VendaHelper.criarVendaEntity;
import static com.javacar.lojadecarro.factory.helper.VendaHelper.criarVendaRequest;
import static org.assertj.core.api.Assertions.assertThat;

class VendaMapperTest extends MapperTest {
    @Autowired
    private VendasMapper mapper;

    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarVendaRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull();

        assertThat(entity.getValorVenda())
                .isEqualTo(request.valorVenda());

    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarVendaEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        VendaResponse::id,
                        VendaResponse::valorVenda
                )
                .containsExactly(
                        1L,
                        BigDecimal.valueOf(200000)
                );
    }

}
