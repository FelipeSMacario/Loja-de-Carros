package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.RoleResponse;
import com.javacar.lojadecarro.factory.helper.RoleHelper;
import com.javacar.lojadecarro.mapper.RoleMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RoleMapperTest extends MapperTest {
    @Autowired
    private RoleMapper mapper;

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = RoleHelper.criarRoleEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        RoleResponse::id,
                        RoleResponse::nome,
                        RoleResponse::ativo
                )
                .containsExactly(
                        1L,
                        "ADMIN",
                        true
                );
    }

}
