package com.javacar.lojadecarro.service;

import com.javacar.lojadecarro.entity.Role;
import com.javacar.lojadecarro.enums.StatusFiltro;
import com.javacar.lojadecarro.exception.business.BusinessException;
import com.javacar.lojadecarro.exception.notfound.NotFoundException;
import com.javacar.lojadecarro.factory.role.RoleTestContext;
import com.javacar.lojadecarro.mapper.RoleMapper;
import com.javacar.lojadecarro.repository.RoleRepository;
import com.javacar.lojadecarro.validation.EntityValidation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.javacar.lojadecarro.enums.Entidade.ROLE;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertBusinessResponseError;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertNotFoundResponseError;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.assertRoleResponse;
import static com.javacar.lojadecarro.factory.helper.RoleHelper.criarRoleEntity;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do serviço de role")
public class RoleServiceTest {
    @Mock
    private RoleRepository roleRepository;
    @Spy
    private EntityValidation entityValidation;
    @Mock
    private RoleMapper roleMapper;
    @InjectMocks
    private RolesService rolesService;

    @Nested
    @DisplayName("Testes de listar roles")
    class ListarRoles {

        @Test
        @DisplayName("Deve listar as roles ativas")
        void listarRolesAtivas() {
            //Arrange
            var roleContext = new RoleTestContext();
            var role1 = roleContext.mockEntidade1(true);
            var role2 = roleContext.mockEntidade2(true);

            var response1 = roleContext.mockResponse1(true);
            var response2 = roleContext.mockResponse2(true);
            var roleList = List.of(role1, role2);

            when(roleRepository.findByAtivo(true))
                    .thenReturn(roleList);

            when(roleMapper.toResponse(role1))
                    .thenReturn(response1);

            when(roleMapper.toResponse(role2))
                    .thenReturn(response2);

            //ACT
            var resultado = rolesService.listar(StatusFiltro.ATIVAS);
            //Assert
            assertRoleResponse(resultado, true, true);

            verify(roleRepository).findByAtivo(true);
            verify(roleRepository, never()).findAll();
            verify(roleMapper).toResponse(role1);
            verify(roleMapper).toResponse(role2);

            verifyNoMoreInteractions(roleRepository, roleMapper);

        }

        @Test
        @DisplayName("Deve listar as roles inativas")
        void listarRolesInativas() {
            //Arrange
            var roleContext = new RoleTestContext();
            var role1 = roleContext.mockEntidade1(false);
            var role2 = roleContext.mockEntidade2(false);

            var response1 = roleContext.mockResponse1(false);
            var response2 = roleContext.mockResponse2(false);

            var roleList = List.of(role1, role2);

            when(roleRepository.findByAtivo(false))
                    .thenReturn(roleList);

            when(roleMapper.toResponse(role1))
                    .thenReturn(response1);
            when(roleMapper.toResponse(role2))
                    .thenReturn(response2);

            //ACT
            var resultado = rolesService.listar(StatusFiltro.INATIVAS);
            //Assert
            assertRoleResponse(resultado, false, false);

            verify(roleRepository).findByAtivo(false);
            verify(roleRepository, never()).findAll();
            verify(roleMapper).toResponse(role1);
            verify(roleMapper).toResponse(role2);

            verifyNoMoreInteractions(roleRepository, roleMapper);

        }

        @Test
        @DisplayName("Deve listar todas as roles")
        void listarTodasRoles() {
            //Arrange
            var roleContext = new RoleTestContext();
            var role1 = roleContext.mockEntidade1(true);
            var role2 = roleContext.mockEntidade2(false);

            var response1 = roleContext.mockResponse1(true);
            var response2 = roleContext.mockResponse2(false);

            var roleList = List.of(role1, role2);

            when(roleRepository.findAll())
                    .thenReturn(roleList);

            when(roleMapper.toResponse(role1))
                    .thenReturn(response1);
            when(roleMapper.toResponse(role2))
                    .thenReturn(response2);

            //ACT
            var resultado = rolesService.listar(StatusFiltro.TODAS);
            //Assert
            assertRoleResponse(resultado, true, false);
            verify(roleRepository, never()).findByAtivo(anyBoolean());
            verify(roleRepository).findAll();
            verify(roleMapper).toResponse(role1);
            verify(roleMapper).toResponse(role2);

            verifyNoMoreInteractions(roleRepository, roleMapper);

        }
    }

    @Nested
    @DisplayName("Testes da busca da entidade role")
    class BuscaEntidadeRole {

        @Test
        @DisplayName("Deve buscar a entidade role")
        void buscaEntidadeRole() {
            //Arrange
            var role = criarRoleEntity();
            when(roleRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.of(role));
            //ACT
            var resultado = rolesService.buscaRole(ID_VALIDO);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .extracting(
                            Role::getId,
                            Role::getNome,
                            Role::isAtivo
                    ).containsExactly(
                            ID_VALIDO,
                            "ADMIN",
                            true
                    );

            verify(roleRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(roleRepository);
        }

        @Test
        @DisplayName("Deve lançar role não encontrada")
        void deveLancarRoleNaoEncontrada() {
            //Arrange
            when(roleRepository.findById(ID_VALIDO))
                    .thenReturn(Optional.empty());
            //ACT
            var excecao = assertThrows(NotFoundException.class,
                    () -> rolesService.buscaRole(ID_VALIDO));
            //Assert
            assertNotFoundResponseError(excecao, ROLE, ID_VALIDO);

            verify(roleRepository).findById(ID_VALIDO);
            verifyNoMoreInteractions(roleRepository);
        }
    }

    @Nested
    @DisplayName("Testes da listagem de roles por IDs")
    class BuscaRolesPorIds {

        @Test
        @DisplayName("Deve listar as roles por IDs")
        void deveListarAsRolesPorIds() {
            //Arrange
            var listIds = List.of(1L, 2L);
            var roleContext = new RoleTestContext();
            var role1 = roleContext.mockEntidade1(true);
            var role2 = roleContext.mockEntidade2(false);

            var roleList = List.of(role1, role2);

            when(roleRepository.findAllByIdIn(listIds))
                    .thenReturn(roleList);
            //ACT
            var resultado = rolesService.buscaRoles(listIds);
            //Assert
            assertThat(resultado)
                    .isNotNull()
                    .hasSize(2)
                    .extracting(
                            Role::getId,
                            Role::getNome,
                            Role::isAtivo
                    ).containsExactly(
                            tuple(ID_VALIDO, "ADMIN", true),
                            tuple(2L, "VENDEDOR", false)
                    );

            verify(roleRepository).findAllByIdIn(listIds);
            verifyNoMoreInteractions(roleRepository);
        }
        @Test
        @DisplayName("Deve lançar exceção ao buscar roles não existente")
        void deveLancarExcecaoAoBuscarRolesNaoExistente() {
            //Arrange
            var listIds = List.of(1L, 2L, 3L);

            when(roleRepository.findAllByIdIn(listIds))
                    .thenThrow(new BusinessException("Uma ou mais roles informadas não foram encontradas."));
            //ACT
            var resultado = assertThrows(BusinessException.class,
                    () -> rolesService.buscaRoles(listIds));
            //Assert
            assertBusinessResponseError(resultado, "Uma ou mais roles informadas não foram encontradas.");

            verify(roleRepository).findAllByIdIn(listIds);
            verifyNoMoreInteractions(roleRepository);
        }
    }
}
