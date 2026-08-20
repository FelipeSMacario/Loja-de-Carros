package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.factory.role.RoleResponseFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioRequestFactory;
import com.javacar.lojadecarro.factory.usuario.UsuarioResponseFactory;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public final class UsuarioHelper extends BaseHelper {
    public static UsuarioRequest criarUsuarioRequest() {
        return UsuarioRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Usuario criarUsuarioEntity() {
        return UsuarioEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static UsuarioResponse criarUsuarioResponse() {
        return UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static void assertUsuarioResponse(UsuarioResponse response) {
        assertThat(response)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email,
                        UsuarioResponse::cpf,
                        UsuarioResponse::ativo
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com",
                        "12345678901",
                        true
                );

    }
    public static void assertUsuarioResponseInativo(UsuarioResponse response) {
        assertThat(response)
                .isNotNull()
                .extracting(
                        UsuarioResponse::id,
                        UsuarioResponse::nome,
                        UsuarioResponse::email,
                        UsuarioResponse::cpf,
                        UsuarioResponse::ativo
                ).containsExactly(
                        ID_VALIDO,
                        "Felipe",
                        "felipesmacario@gmail.com",
                        "12345678901",
                        false
                );

    }

    public static UsuarioRolesResponse criarUsuarioRolesResponse() {
        var listRolesResponse = List.of(
                RoleResponseFactory
                        .criarResponse()
                .comTodosOsCampos()
                        .build(),
                RoleResponseFactory
                        .criarResponse()
                        .comNome("VENDEDOR")
                        .comId(2L)
                        .comAtivo(true)
                        .build()

        );

        return new UsuarioRolesResponse(1L,
                "Felipe Soares Macário",
                "12345678901",
                listRolesResponse);
    }

    public static UsuarioRolesResponse criarUsuarioRolesResponseDesvincula() {
        var listRolesResponse = List.of(
                RoleResponseFactory
                        .criarResponse()
                        .comNome("VENDEDOR")
                        .comId(2L)
                        .comAtivo(true)
                        .build()

        );

        return new UsuarioRolesResponse(1L,
                "Felipe Soares Macário",
                "1234567890",
                listRolesResponse);
    }

    public static void assertUsuario(ResultActions result,
                                     ResultMatcher status,
                                     Long id,
                                     String nome,
                                     boolean ativo,
                                     String email,
                                     String cpf) throws Exception {
        assertResult(result, status, id, nome, ativo);

        result
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.cpf").value(cpf));
    }

    public static void assertAlteracaoSenha(ResultActions result,
                                     String email,
                                     String mensagem) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.mensagem").value(mensagem));
    }

    public static void assertListUsuario(ResultActions result,
                                         Long primeiroId,
                                         Long segundoId,
                                         String primeiroNome,
                                         String segundoNome,
                                         boolean primeiroAtivo,
                                         boolean segundoAtivo,
                                         String primeiroEmail,
                                         String segundoEmail,
                                         String primeiroCpf,
                                         String segundoCpf) throws Exception {
        assertList(result, primeiroId, segundoId, primeiroNome, segundoNome, primeiroAtivo, segundoAtivo);
        result
                .andExpect(jsonPath("$[0].email").value(primeiroEmail))
                .andExpect(jsonPath("$[1].email").value(segundoEmail))
                .andExpect(jsonPath("$[0].cpf").value(primeiroCpf))
                .andExpect(jsonPath("$[1].cpf").value(segundoCpf));
    }
    public static void assertUsuarioRole(ResultActions result, Long id, String nome, String cpf) throws Exception {
        result
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.cpf").value(cpf));
    }

}
