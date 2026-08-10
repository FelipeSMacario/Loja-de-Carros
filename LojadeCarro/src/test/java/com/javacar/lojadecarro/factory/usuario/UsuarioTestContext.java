package com.javacar.lojadecarro.factory.usuario;

import com.javacar.lojadecarro.dto.request.UsuarioRequest;
import com.javacar.lojadecarro.dto.request.UsuarioRolesRequest;
import com.javacar.lojadecarro.dto.request.UsuarioUpdateRequest;
import com.javacar.lojadecarro.dto.response.UsuarioResponse;
import com.javacar.lojadecarro.dto.response.UsuarioResumoResponse;
import com.javacar.lojadecarro.dto.response.UsuarioRolesResponse;

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
                .comCpf("1234567890")
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


}
