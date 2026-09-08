package com.javacar.lojadecarro.factory.mapper;

import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.Month;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioEntity;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioRequest;
import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest extends MapperTest {
    @Autowired
    private UsuarioMapper mapper;


    @Test
    @DisplayName("Deve converter a requisição para entidade")
    void deveConverterRequestParaEntity() {
        var request = criarUsuarioRequest();

        var entity = mapper.toEntity(request);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Usuario::getNome,
                        Usuario::getCpf,
                        Usuario::getDataNascimento
                )
                .containsExactly(
                        "Felipe Soares Macário",
                        "15152736799",
                        LocalDate.of(1991, Month.MAY, 14)

                );

    }

    @Test
    @DisplayName("Deve converter a entidade para resposta")
    void deveConverterEntityParaResponse() {
        var entity = criarUsuarioEntity();
        var response = mapper.toResponse(entity);

        assertThat(response)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::cpf,
                        UsuarioResponse::email,
                        UsuarioResponse::ativo
                )
                .containsExactly(
                        1L,
                        "Felipe Soares Macário",
                        "1234567890",
                        "felipesmacario@gmail.com",
                        true
                );
    }

    @Test
    @DisplayName("Deve atualizar a entidade")
    void deveAtualizarEntity() {
        var request = UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .comNome("Cristiano Ronaldo")
                .build();

        var entity = criarUsuarioEntity();

        mapper.toUpdate(request, entity);

        assertThat(entity)
                .isNotNull()
                .extracting(
                        Usuario::getId,
                        Usuario::getCpf,
                        Usuario::getDataNascimento,
                        Usuario::getNome,
                        Usuario::isAtivo
                )
                .containsExactly(
                        entity.getId(),
                        entity.getCpf(),
                        entity.getDataNascimento(),
                        request.nome(),
                        entity.isAtivo()
                );
    }


}
