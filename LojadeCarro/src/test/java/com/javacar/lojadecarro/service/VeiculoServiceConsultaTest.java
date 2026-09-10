package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.helper.VeiculoTestContext;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.USUARIO;
import static com.javacar.lojadecarro.enums.Entidade.VEICULO;
import static com.javacar.lojadecarro.enums.StatusVeiculo.*;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.assertVeiculoResponse;
import static com.javacar.lojadecarro.support.TestConstants.ID_INVALIDO;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Testes das consultas de veículos")
public class VeiculoServiceConsultaTest extends AbstractVeiculoServiceTest {
    private final PageRequest pageable =
            PageRequest.of(0, 10);

    private final Long imagem = 1L;

    @Nested
    @DisplayName("Testes da listagem de veículos")
    class Listar {
        @Test
        @DisplayName("Deve listar todos os veiculos")
        void deveListarTodosVeiculos() {
            //Arrange
            var pagina = veiculosPage(pageable);
            var veiculo1 = pagina.getContent().getFirst();
            var veiculo2 = pagina.getContent().get(1);
            var veiculo3 = pagina.getContent().get(2);
            var veiculo4 = pagina.getContent().getLast();

            var imagemPrincipal1 = criarImagemPrincipal(10L, veiculo1);
            var imagemPrincipal2 = criarImagemPrincipal(20L, veiculo2);
            var imagemPrincipal3 = criarImagemPrincipal(30L, veiculo3);
            var imagemPrincipal4 = criarImagemPrincipal(40L, veiculo4);

            var idsVeiculos = List.of(
                    veiculo1.getId(),
                    veiculo2.getId(),
                    veiculo3.getId(),
                    veiculo4.getId()
            );
            var responseList =
                    veiculosResponseList(imagemPrincipal1.getId(),
                            imagemPrincipal2.getId(),
                            imagemPrincipal3.getId(),
                            imagemPrincipal4.getId());


            when(veiculoRepository.findAll(pageable))
                    .thenReturn(pagina);

            when(imagensRepository
                    .findByVeiculo_IdInAndPrincipalTrue(idsVeiculos))
                    .thenReturn(List.of(
                            imagemPrincipal1,
                            imagemPrincipal2,
                            imagemPrincipal3,
                            imagemPrincipal4
                    ));


            when(veiculoMapper.toResponse(pagina.getContent().getFirst(), imagemPrincipal1.getId()))
                    .thenReturn(responseList.getFirst());
            when(veiculoMapper.toResponse(pagina.getContent().get(1), imagemPrincipal2.getId()))
                    .thenReturn(responseList.get(1));
            when(veiculoMapper.toResponse(pagina.getContent().get(2), imagemPrincipal3.getId()))
                    .thenReturn(responseList.get(2));
            when(veiculoMapper.toResponse(pagina.getContent().getLast(), imagemPrincipal4.getId()))
                    .thenReturn(responseList.getLast());

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, null);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(4)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, VENDIDO),
                            tuple(2L, DISPONIVEL),
                            tuple(3L, PAUSADO),
                            tuple(4L, VENDIDO)
                    );

            verify(veiculoRepository).findAll(pageable);
            verify(veiculoRepository, never()).findByStatusVeiculo(any(StatusVeiculo.class), eq(pageable));
            verify(imagensRepository)
                    .findByVeiculo_IdInAndPrincipalTrue(
                            idsVeiculos
                    );
            verify(veiculoMapper).toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            );

            verify(veiculoMapper).toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            );
            verify(veiculoMapper).toResponse(
                    veiculo3,
                    imagemPrincipal3.getId()
            );
            verify(veiculoMapper).toResponse(
                    veiculo4,
                    imagemPrincipal4.getId()
            );

            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagensRepository,
                    veiculoMapper
            );
        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar veiculos por status")
        void deveListarVeiculosPorStatus(StatusVeiculo statusVeiculo) {
            //Arrange
            var veiculoPage = veiculosPageStatus(pageable, statusVeiculo);

            var veiculo1 = veiculoPage.getContent().getFirst();
            var veiculo2 = veiculoPage.getContent().getLast();

            var imagemPrincipal1 = new Imagem();
            imagemPrincipal1.setId(10L);
            imagemPrincipal1.setPrincipal(true);
            imagemPrincipal1.setVeiculo(veiculo1);

            var imagemPrincipal2 = new Imagem();
            imagemPrincipal2.setId(20L);
            imagemPrincipal2.setPrincipal(true);
            imagemPrincipal2.setVeiculo(veiculo2);

            var veiculoResponseList = veiculosResponseListStatus(
                    statusVeiculo,
                    imagemPrincipal1.getId(),
                    imagemPrincipal2.getId()
            );
            var idsVeiculos = List.of(
                    veiculo1.getId(),
                    veiculo2.getId()
            );


            when(veiculoRepository.findByStatusVeiculo(statusVeiculo, pageable))
                    .thenReturn(veiculoPage);

            when(imagensRepository
                    .findByVeiculo_IdInAndPrincipalTrue(idsVeiculos))
                    .thenReturn(List.of(
                            imagemPrincipal1,
                            imagemPrincipal2
                    ));

            when(veiculoMapper.toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            )).thenReturn(veiculoResponseList.getFirst());

            when(veiculoMapper.toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            )).thenReturn(veiculoResponseList.getLast());

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, statusVeiculo);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo,
                            VeiculoResponse::imagemPrincipalId
                    )
                    .containsExactly(
                            tuple(
                                    veiculo1.getId(),
                                    statusVeiculo,
                                    imagemPrincipal1.getId()
                            ),
                            tuple(
                                    veiculo2.getId(),
                                    statusVeiculo,
                                    imagemPrincipal2.getId()
                            )
                    );

            verify(veiculoRepository, never()).findAll(pageable);
            verify(veiculoRepository).findByStatusVeiculo(statusVeiculo, pageable);
            verify(imagensRepository)
                    .findByVeiculo_IdInAndPrincipalTrue(
                            idsVeiculos
                    );
            verify(veiculoMapper).toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            );

            verify(veiculoMapper).toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            );

            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagensRepository,
                    veiculoMapper
            );
        }

        @Test
        @DisplayName("Deve retornar página vazia sem buscar imagens")
        void deveRetornarUmaListaVazia() {
            //Arrange
            when(veiculoRepository.findAll(pageable))
                    .thenReturn(Page.empty());

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, null);
            //Assert
            assertThat(resultado)
                    .isEmpty();

            verify(veiculoRepository).findAll(pageable);
            verify(veiculoRepository, never()).findByStatusVeiculo(any(StatusVeiculo.class), eq(pageable));
            verify(veiculoMapper, never()).toResponse(any(), anyLong());

            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagensRepository,
                    veiculoMapper
            );
        }

        @Test
        @DisplayName("Deve retornar veículos sem imagem principal")
        void deveRetornarVeiculosSemImagemPrincipal() {
            //Arrange
            var pagina = veiculosPage(pageable);
            var veiculos = pagina.getContent();

            var idsVeiculos = veiculos
                    .stream()
                    .map(Veiculo::getId)
                    .toList();

            var responseList =
                    veiculosResponseList(null, null, null, null);

            when(veiculoRepository.findAll(pageable))
                    .thenReturn(pagina);

            when(imagensRepository.findByVeiculo_IdInAndPrincipalTrue(idsVeiculos))
                    .thenReturn(Collections.emptyList());

            when(veiculoMapper.toResponse(veiculos.get(0), null))
                    .thenReturn(responseList.get(0));

            when(veiculoMapper.toResponse(veiculos.get(1), null))
                    .thenReturn(responseList.get(1));

            when(veiculoMapper.toResponse(veiculos.get(2), null))
                    .thenReturn(responseList.get(2));

            when(veiculoMapper.toResponse(veiculos.get(3), null))
                    .thenReturn(responseList.get(3));

            //ACT
            var resultado = veiculoService.listarAdministrativo(pageable, null);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(4)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::imagemPrincipalId,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, null, VENDIDO),
                            tuple(2L, null, DISPONIVEL),
                            tuple(3L, null, PAUSADO),
                            tuple(4L, null, VENDIDO)
                    );

            verify(veiculoRepository).findAll(pageable);
            verify(veiculoRepository, never()).findByStatusVeiculo(any(StatusVeiculo.class), eq(pageable));
            verify(imagensRepository).findByVeiculo_IdInAndPrincipalTrue(idsVeiculos);
            verify(veiculoMapper).toResponse(veiculos.get(0), null);
            verify(veiculoMapper).toResponse(veiculos.get(1), null);
            verify(veiculoMapper).toResponse(veiculos.get(2), null);
            verify(veiculoMapper).toResponse(veiculos.get(3), null);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagensRepository,
                    veiculoMapper
            );
        }
    }

    @Nested
    @DisplayName("Testes da listagem de veículos disponíveis")
    class ListarDisponveis {
        @Test
        @DisplayName("Deve listar veiculos disponiveis")
        void deveListarVeiculosDisponiveis() {
            //Arrange
            var veiculoPage = veiculosPageStatus(pageable, DISPONIVEL);
            var veiculo1 = veiculoPage.getContent().getFirst();
            var veiculo2 = veiculoPage.getContent().getLast();

            var imagemPrincipal1 = criarImagemPrincipal(10L, veiculo1);
            var imagemPrincipal2 = criarImagemPrincipal(20L, veiculo2);

            var veiculoResponseList = veiculosResponseListStatus(
                    DISPONIVEL,
                    imagemPrincipal1.getId(),
                    imagemPrincipal2.getId()
            );
            var idsVeiculos = List.of(
                    veiculo1.getId(),
                    veiculo2.getId()
            );


            when(veiculoRepository.findByStatusVeiculo(DISPONIVEL, pageable))
                    .thenReturn(veiculoPage);

            when(imagensRepository
                    .findByVeiculo_IdInAndPrincipalTrue(idsVeiculos))
                    .thenReturn(List.of(
                            imagemPrincipal1,
                            imagemPrincipal2
                    ));


            when(veiculoMapper.toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            )).thenReturn(veiculoResponseList.getFirst());

            when(veiculoMapper.toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            )).thenReturn(veiculoResponseList.getLast());

            //ACT
            var resultado = veiculoService.listarAtivos(pageable);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, DISPONIVEL),
                            tuple(2L, DISPONIVEL)
                    );

            verify(veiculoRepository).findByStatusVeiculo(DISPONIVEL, pageable);
            verify(imagensRepository)
                    .findByVeiculo_IdInAndPrincipalTrue(
                            idsVeiculos
                    );
            verify(veiculoMapper).toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            );

            verify(veiculoMapper).toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            );

            verifyNoMoreInteractions(
                    veiculoRepository,
                    imagensRepository,
                    veiculoMapper
            );
        }
    }

    @Nested
    @DisplayName("Testes da listagem dos veículos do usuário autenticado")
    class ListarVeiculosUsuarioAutenticado {
        @Test
        @DisplayName("Deve listar todos os veículos do usuário autenticado")
        void deveListarVeiculosUsuarioAutenticado() {
            //Arrange
            var cx = new VeiculoTestContext();
            var pagina = veiculosPage(pageable);
            var veiculo1 = pagina.getContent().getFirst();
            var veiculo2 = pagina.getContent().get(1);
            var veiculo3 = pagina.getContent().get(2);
            var veiculo4 = pagina.getContent().getLast();

            var imagemPrincipal1 = criarImagemPrincipal(10L, veiculo1);
            var imagemPrincipal2 = criarImagemPrincipal(20L, veiculo2);
            var imagemPrincipal3 = criarImagemPrincipal(30L, veiculo3);
            var imagemPrincipal4 = criarImagemPrincipal(40L, veiculo4);

            var idsVeiculos = List.of(
                    veiculo1.getId(),
                    veiculo2.getId(),
                    veiculo3.getId(),
                    veiculo4.getId()
            );
            var responseList =
                    veiculosResponseList(imagemPrincipal1.getId(),
                            imagemPrincipal2.getId(),
                            imagemPrincipal3.getId(),
                            imagemPrincipal4.getId());

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.findByVendedor_Id(cx.usuario.getId(), pageable))
                    .thenReturn(pagina);

            when(imagensRepository
                    .findByVeiculo_IdInAndPrincipalTrue(idsVeiculos))
                    .thenReturn(List.of(
                            imagemPrincipal1,
                            imagemPrincipal2,
                            imagemPrincipal3,
                            imagemPrincipal4
                    ));

            when(veiculoMapper.toResponse(pagina.getContent().getFirst(), imagemPrincipal1.getId()))
                    .thenReturn(responseList.getFirst());
            when(veiculoMapper.toResponse(pagina.getContent().get(1), imagemPrincipal2.getId()))
                    .thenReturn(responseList.get(1));
            when(veiculoMapper.toResponse(pagina.getContent().get(2), imagemPrincipal3.getId()))
                    .thenReturn(responseList.get(2));
            when(veiculoMapper.toResponse(pagina.getContent().getLast(), imagemPrincipal4.getId()))
                    .thenReturn(responseList.getLast());
            //ACT
            var response = veiculoService.listarMeusAnuncios(pageable, cx.usuario.getId(), null);
            //Assert
            assertThat(response)
                    .isNotNull()
                    .hasSize(4)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, VENDIDO),
                            tuple(2L, DISPONIVEL),
                            tuple(3L, PAUSADO),
                            tuple(4L, VENDIDO)
                    );

            assertThat(pagina.getContent())
                    .extracting(v -> v.getVendedor().getId())
                    .allMatch(id -> id.equals(cx.usuario.getId()));

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository).findByVendedor_Id(cx.usuario.getId(), pageable);
            verify(veiculoRepository, never()).findByVendedor_IdAndStatusVeiculo(anyLong(), any(), any());
            verify(imagensRepository)
                    .findByVeiculo_IdInAndPrincipalTrue(
                            idsVeiculos
                    );
            verify(veiculoMapper).toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            );

            verify(veiculoMapper).toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            );
            verify(veiculoMapper).toResponse(
                    veiculo3,
                    imagemPrincipal3.getId()
            );
            verify(veiculoMapper).toResponse(
                    veiculo4,
                    imagemPrincipal4.getId()
            );

            verifyNoMoreInteractions(usuarioService, veiculoRepository, veiculoMapper, imagensRepository);
        }

        @ParameterizedTest
        @EnumSource(StatusVeiculo.class)
        @DisplayName("Deve listar todos os veículos do usuário autenticado por status")
        void deveListarVeiculosUsuarioAutenticadoPorStatus(StatusVeiculo statusVeiculo) {
            //Arrange
            var cx = new VeiculoTestContext();
            var veiculoPage = veiculosPageStatus(pageable, statusVeiculo);
            var veiculo1 = veiculoPage.getContent().getFirst();
            var veiculo2 = veiculoPage.getContent().getLast();

            var imagemPrincipal1 = new Imagem();
            imagemPrincipal1.setId(10L);
            imagemPrincipal1.setPrincipal(true);
            imagemPrincipal1.setVeiculo(veiculo1);

            var imagemPrincipal2 = new Imagem();
            imagemPrincipal2.setId(20L);
            imagemPrincipal2.setPrincipal(true);
            imagemPrincipal2.setVeiculo(veiculo2);

            var veiculoResponseList = veiculosResponseListStatus(
                    statusVeiculo,
                    imagemPrincipal1.getId(),
                    imagemPrincipal2.getId()
            );
            var idsVeiculos = List.of(
                    veiculo1.getId(),
                    veiculo2.getId()
            );

            when(usuarioService.buscaUsuarioAtivo(ID_VALIDO))
                    .thenReturn(cx.usuario);
            when(veiculoRepository.findByVendedor_IdAndStatusVeiculo(cx.usuario.getId(), statusVeiculo, pageable))
                    .thenReturn(veiculoPage);
            when(imagensRepository
                    .findByVeiculo_IdInAndPrincipalTrue(idsVeiculos))
                    .thenReturn(List.of(
                            imagemPrincipal1,
                            imagemPrincipal2
                    ));

            when(veiculoMapper.toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            )).thenReturn(veiculoResponseList.getFirst());

            when(veiculoMapper.toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            )).thenReturn(veiculoResponseList.getLast());
            //ACT
            var response = veiculoService.listarMeusAnuncios(pageable, cx.usuario.getId(), statusVeiculo);
            //Assert
            assertThat(response)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            VeiculoResponse::id,
                            VeiculoResponse::statusVeiculo
                    ).containsExactly(
                            tuple(ID_VALIDO, statusVeiculo),
                            tuple(2L, statusVeiculo)
                    );

            assertThat(veiculoPage.getContent())
                    .extracting(v -> v.getVendedor().getId())
                    .allMatch(id -> id.equals(cx.usuario.getId()));

            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository, never()).findByVendedor_Id(cx.usuario.getId(), pageable);
            verify(veiculoRepository).findByVendedor_IdAndStatusVeiculo(cx.usuario.getId(), statusVeiculo, pageable);
            verify(imagensRepository)
                    .findByVeiculo_IdInAndPrincipalTrue(
                            idsVeiculos
                    );
            verify(veiculoMapper).toResponse(
                    veiculo1,
                    imagemPrincipal1.getId()
            );

            verify(veiculoMapper).toResponse(
                    veiculo2,
                    imagemPrincipal2.getId()
            );

            verifyNoMoreInteractions(usuarioService, veiculoRepository, veiculoMapper, imagensRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção de usuário inativo")
        void deveLancarExcecaoUsuarioInativo() {
            //Arrange
            var cx = new VeiculoTestContext();
            var usuarioId = cx.usuario.getId();
            when(usuarioService.buscaUsuarioAtivo(cx.usuario.getId()))
                    .thenThrow(new NotFoundException(USUARIO, cx.usuario.getId()));
            //ACT
            var exception = assertThrows(NotFoundException.class,
                    () -> veiculoService.listarMeusAnuncios(pageable, usuarioId, null));
            //Assert
            assertNotFoundResponseError(exception, USUARIO, usuarioId);
            verify(usuarioService).buscaUsuarioAtivo(ID_VALIDO);
            verify(veiculoRepository, never()).findByVendedor_Id(anyLong(), any());
            verify(veiculoRepository, never()).findByVendedor_IdAndStatusVeiculo(anyLong(), any(), any());
            verify(veiculoMapper, never()).toResponse(any(), anyLong());
            verifyNoMoreInteractions(usuarioService, veiculoRepository, veiculoMapper);

        }
    }

    @Nested
    @DisplayName("Testes da busca do veículo disponível por ID")
    class Buscar {
        @Test
        @DisplayName("Deve buscar o veículo disponivel por ID")
        void deveBuscarVeiculoPorId() {
            //Arrange
            var cx = new VeiculoTestContext();

            when(veiculoRepository.findByIdAndStatusVeiculo(ID_VALIDO, DISPONIVEL))
                    .thenReturn(Optional.of(cx.entity));

            when(imagensRepository.findByVeiculo_IdAndPrincipalTrue(cx.entity.getId()))
                    .thenReturn(Optional.of(cx.imagens.getFirst()));

            when(veiculoMapper.toResponse(cx.entity, imagem))
                    .thenReturn(cx.response);
            //ACT
            var resultado = veiculoService.buscarPorId(ID_VALIDO);
            //Assert
            assertVeiculoResponse(resultado);

            verify(veiculoRepository).findByIdAndStatusVeiculo(ID_VALIDO, DISPONIVEL);
            verify(imagensRepository).findByVeiculo_IdAndPrincipalTrue(cx.entity.getId());
            verify(veiculoMapper).toResponse(cx.entity, imagem);

            verifyNoMoreInteractions(
                    veiculoRepository,
                    veiculoMapper);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar veículo indisponível")
        void deveLancarExcecaoAOBuscarVeiculo() {
            //Arrange
            when(veiculoRepository.findByIdAndStatusVeiculo(ID_INVALIDO, DISPONIVEL))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> veiculoService.buscarPorId(ID_INVALIDO));
            //Assert
            assertNotFoundResponseError(excecao, VEICULO, ID_INVALIDO);
            verify(veiculoRepository).findByIdAndStatusVeiculo(ID_INVALIDO, DISPONIVEL);
            verify(veiculoMapper, never()).toResponse(any(), anyLong());

            verifyNoMoreInteractions(veiculoRepository, veiculoMapper);

        }
    }

    private Page<Veiculo> veiculosPage(PageRequest pageable) {
        var entity1 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comStatus(VENDIDO)
                .build();
        var entity2 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(DISPONIVEL)
                .build();

        var entity3 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(3L)
                .comStatus(PAUSADO)
                .build();

        var entity4 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(4L)
                .comStatus(VENDIDO)
                .build();

        return new PageImpl<>(
                List.of(entity1, entity2, entity3, entity4),
                pageable,
                4
        );
    }

    private Page<Veiculo> veiculosPageStatus(PageRequest pageable, StatusVeiculo status) {
        var entity1 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comStatus(status)
                .build();
        var entity2 = VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(status)
                .build();

        return new PageImpl<>(
                List.of(entity1, entity2),
                pageable,
                2
        );
    }


    private List<VeiculoResponse> veiculosResponseList(Long idImagemPrincipal1,
                                                       Long idImagemPrincipal2,
                                                       Long idImagemPrincipal3,
                                                       Long idImagemPrincipal4) {
        var response1 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comStatus(VENDIDO)
                .comIdImagemPrincipal(idImagemPrincipal1)
                .build();

        var response2 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(DISPONIVEL)
                .comIdImagemPrincipal(idImagemPrincipal2)
                .build();

        var response3 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(3L)
                .comStatus(PAUSADO)
                .comIdImagemPrincipal(idImagemPrincipal3)
                .build();

        var response4 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(4L)
                .comStatus(VENDIDO)
                .comIdImagemPrincipal(idImagemPrincipal4)
                .build();

        return List.of(response1, response2, response3, response4);
    }

    private List<VeiculoResponse> veiculosResponseListStatus(StatusVeiculo status,
                                                             Long idImagemPrincipal1,
                                                             Long idImagemPrincipal2) {
        var response1 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comStatus(status)
                .comIdImagemPrincipal(idImagemPrincipal1)
                .build();

        var response2 = VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .comId(2L)
                .comStatus(status)
                .comIdImagemPrincipal(idImagemPrincipal2)
                .build();

        return List.of(response1, response2);
    }

    private Imagem criarImagemPrincipal(Long idImagemPrincipal, Veiculo veiculo) {
        var imagemPrincipal = new Imagem();
        imagemPrincipal.setId(idImagemPrincipal);
        imagemPrincipal.setPrincipal(true);
        imagemPrincipal.setVeiculo(veiculo);

        return imagemPrincipal;
    }
}
