package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRolesRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.*;
import com.javacar.lojadecarro.entity.Usuario;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.*;

public class UsuarioTestContext {
    public final UsuarioRequest request = criarUsuarioRequest();
    public final UsuarioRequest requestIncompleto = UsuarioRequestFactory.criarRequest().build();
    public final UsuarioResponse response = criarUsuarioResponse();
    public final UsuarioRolesRequest listRoles = new UsuarioRolesRequest(List.of(1L, 2L, 3L, 4L));
    public final UsuarioRolesRequest listRolesIncompleta = new UsuarioRolesRequest(null);
    public final UsuarioRolesResponse usuarioRolesResponse = criarUsuarioRolesResponse();

    public static Usuario criarUsuario(Long id, String nome, String email, String cpf, String senha, boolean ativo){
        return UsuarioEntityFactory
                .criarEntity()
                .comId(id)
                .comNome(nome)
                .comEmail(email)
                .comCPF(cpf)
                .comSenha(senha)
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
                .comEmail("batman@gmail.com")
                .comPassword("IAmBatman")
                .comCPF("44444444445")
                .comDataNascimento(LocalDate.of(1982, Month.JANUARY, 2))
                .build();
    }
    public static UsuarioRequest criarUsuarioCPFRepetidoValido() {
        return UsuarioRequestFactory
                .criarRequest()
                .comNome("Batman")
                .comEmail("batmaimMorcegao@gmail.com")
                .comPassword("IAmBatman")
                .comCPF("15052036000")
                .comDataNascimento(LocalDate.of(1982, Month.JANUARY, 2))
                .build();
    }
    public static UsuarioRequest criarUsuarioEmailRepetidoValido() {
        return UsuarioRequestFactory
                .criarRequest()
                .comNome("Batman")
                .comEmail("felipe.vendedor@gmail.com")
                .comPassword("IAmBatman")
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

    public static UsuarioResumoResponse criarUsuarioResumo(Long id, String nome){
        return new UsuarioResumoResponse(id, nome);
    }
    public static UsuarioRolesResponse criarRoleUsuariosResponse(Long id, String nome, String cpf, List<RoleResponse> roles){
        return new UsuarioRolesResponse(id, nome, cpf, roles);
    }

    public static RoleResponse criarRoleResponse(Long id, String nome, boolean ativo){
        return new RoleResponse(id, nome, ativo);
    }
    public static AlteracaoSenhaResponse criarAlteracaoSenhaValido(String senha, String mensagem) {
        return new AlteracaoSenhaResponse(senha, mensagem);
    }
}
