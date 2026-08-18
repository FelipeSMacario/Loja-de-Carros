package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.enums.Entidade;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseHelper {
    public static void assertNotFoundResponseError(NotFoundException exception,
                                                   Entidade entidade,
                                                   Long idInvalido) {
        assertThat(exception)
                .hasMessage(entidade.naoEncontrada() + idInvalido);
    }

    public static void assertBusinessResponseError(BusinessException exception, Entidade entidade) {
        assertThat(exception)
                .hasMessage(entidade.jaAtiva());
    }

    public static void assertBusinessResponseErrorInativa(BusinessException exception, Entidade entidade) {
        assertThat(exception)
                .hasMessage(entidade.jaInativa());
    }

    public static void assertBusinessResponseError(BusinessException exception, String mensagem) {
        assertThat(exception)
                .hasMessage(mensagem);
    }

    public static String mensagemNotFound(Entidade operacao, Long id) {
        return operacao.naoEncontrada() + id;
    }

    public static void assertStatus404(ResultActions result,
                                       Entidade entidade,
                                       Long id) throws Exception {
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value(mensagemNotFound(entidade, id)));
    }

    public static void assertStatus400(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    public static void assertStatus403(ResultActions result) throws Exception {
        result.andExpect(status().isForbidden());
    }

    public static void assertStatus403ProblemDetail(ResultActions result) throws Exception {
        result
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.title").value("Acesso negado"))
                .andExpect(jsonPath("$.detail").value(
                        "Você não possui permissão para realizar esta operação."
                ));
    }

    public static void assertStatus204(ResultActions result) throws Exception {
        result.andExpect(status().isNoContent());
    }

    public static void assertStatus401ProblemDetail(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos."));
    }

    public static void assertStatus401(ResultActions result) throws Exception {
        result.andExpect(status().isUnauthorized());
    }

    public static void assertStatus500(ResultActions result) throws Exception {
        result.andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }

    public static void assertList(ResultActions result,
                                  Long primeiroId,
                                  Long segundoId,
                                  String primeiroNome,
                                  String segundoNome,
                                  boolean primeiroAtivo,
                                  boolean segundoAtivo) throws Exception {
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(primeiroId))
                .andExpect(jsonPath("$[1].id").value(segundoId))
                .andExpect(jsonPath("$[0].nome").value(primeiroNome))
                .andExpect(jsonPath("$[1].nome").value(segundoNome))
                .andExpect(jsonPath("$[0].ativo").value(primeiroAtivo))
                .andExpect(jsonPath("$[1].ativo").value(segundoAtivo));
    }

    public static void assertResult(ResultActions result,
                                    ResultMatcher status,
                                    Long id,
                                    String nome,
                                    boolean ativo) throws Exception {
        result
                .andExpect(status)
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.ativo").value(ativo));
    }

}
