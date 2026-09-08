package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResumoResponse;
import com.javacar.lojadecarro.entity.Usuario;

import java.time.LocalDate;
import java.time.Month;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioRequest;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioResponse;

public class UsuarioTestContext {
    public final UsuarioRequest request = criarUsuarioRequest();
    public final UsuarioRequest requestIncompleto = UsuarioRequestFactory.criarRequest().build();
    public final UsuarioResponse response = criarUsuarioResponse();

    public static Usuario criarUsuario(Long id, String nome, String email, String cpf, boolean ativo) {
        return UsuarioEntityFactory
                .criarEntity()
                .comId(id)
                .comNome(nome)
                .comEmail(email)
                .comCPF(cpf)
                .comAtivo(ativo)
                .build();

    }

    public static UsuarioResponse criaUsuarioResponse(Long id, String nome, String email, String cpf, boolean ativo) {
        return UsuarioResponseFactory
                .criarResponse()
                .comId(id)
                .comNome(nome)
                .comEmail(email)
                .comCpf(cpf)
                .comAtivo(ativo)
                .build();
    }

    public static UsuarioResponse criaUsuarioResponse(boolean ativo) {
        return UsuarioResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comAtivo(ativo)
                .build();
    }

    public static UsuarioResponse criaUsuarioResponse2(boolean ativo) {
        return UsuarioResponseFactory
                .criarResponse()
                .comId(2L)
                .comNome("Goku")
                .comEmail("goku@gmail.com")
                .comCpf("12345678901")
                .comAtivo(ativo)
                .build();
    }

    public static UsuarioRequest criarUsuarioValido() {
        return UsuarioRequestFactory
                .criarRequest()
                .comNome("Batman")
                .comCPF("44444444445")
                .comDataNascimento(LocalDate.of(1982, Month.JANUARY, 2))
                .build();
    }

    public static UsuarioRequest criarUsuarioCPFRepetidoValido() {
        return UsuarioRequestFactory
                .criarRequest()
                .comNome("Batman")
                .comCPF("15052036000")
                .comDataNascimento(LocalDate.of(1982, Month.JANUARY, 2))
                .build();
    }

    public static UsuarioRequest criarUsuarioEmailRepetidoValido() {
        return UsuarioRequestFactory
                .criarRequest()
                .comNome("Batman")
                .comCPF("15052036001")
                .comDataNascimento(LocalDate.of(1982, Month.JANUARY, 2))
                .build();
    }

    public static UsuarioUpdateRequest atualizarUsuarioValido() {
        return new UsuarioUpdateRequest(
                "Superman",
                LocalDate.of(1982, Month.JANUARY, 2),
                "felipe.vendedor@gmail.com"
        );
    }

    public static UsuarioUpdateRequest atualizarUsuarioValido(String nome, LocalDate dataNascimento, String email) {
        return new UsuarioUpdateRequest(
                nome,
                dataNascimento,
                email
        );
    }

    public static UsuarioUpdateRequest atualizarUsuarioEmailInvalido() {
        return new UsuarioUpdateRequest(
                "Superman",
                LocalDate.of(1982, Month.JANUARY, 2),
                "joao.silva@gmail.com"
        );

    }

    public static UsuarioResumoResponse criarUsuarioResumo(Long id, String nome) {
        return new UsuarioResumoResponse(id, nome);
    }
}
