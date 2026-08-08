package com.javacar.lojadecarro.controller;

import com.javacar.lojadecarro.controller.administrativo.AdminRoleController;
import com.javacar.lojadecarro.service.RolesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static com.javacar.lojadecarro.enums.StatusFiltro.ATIVAS;
import static com.javacar.lojadecarro.enums.StatusFiltro.TODAS;
import static com.javacar.lojadecarro.factory.helper.BaseHelper.assertList;
import static com.javacar.lojadecarro.factory.role.RoleTestContext.mockResponse1;
import static com.javacar.lojadecarro.factory.role.RoleTestContext.mockResponse2;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.mockito.Mockito.*;

@WebMvcTest(AdminRoleController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes da controller da role")
public class RoleControllerTest extends BaseControllerTest {
    private static final String URL = "/roles";

    @MockitoBean
    private RolesService rolesService;

    @Nested
    @DisplayName("Testes de listagem")
    class Listar {
        @Test
        @DisplayName("Deve utilizar ATIVAS como status padrão")
        void deveUtilizarAtivasComoStatusPadrao() throws Exception {
            //Arrange
            var response1 = mockResponse1(true);
            var response2 = mockResponse2(true);

            var response = List.of(response1, response2);

            when(rolesService.listar(ATIVAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL);
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "ADMIN",
                    "VENDEDOR",
                    true,
                    true
            );
            verify(rolesService).listar(ATIVAS);
            verifyNoMoreInteractions(rolesService);
        }

        @Test
        @DisplayName("Deve encaminhar o status informado para a service")
        void deveEncaminharStatusTodas() throws Exception {
            //Arrange
            var response1 = mockResponse1(true);
            var response2 = mockResponse2(false);

            var response = List.of(response1, response2);

            when(rolesService.listar(TODAS))
                    .thenReturn(response);
            //Act + Assert
            var resultado = performGet(URL, "status", TODAS.toString());
            assertList(
                    resultado,
                    ID_VALIDO,
                    2L,
                    "ADMIN",
                    "VENDEDOR",
                    true,
                    false
            );
            verify(rolesService).listar(TODAS);
            verifyNoMoreInteractions(rolesService);
        }


    }
}
