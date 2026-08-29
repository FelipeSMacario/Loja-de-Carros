package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.enums.StatusVenda;
import com.javacar.lojadecarro.factory.venda.VendaEntityFactory;
import com.javacar.lojadecarro.factory.venda.VendaRequestFactory;
import com.javacar.lojadecarro.factory.venda.VendaResponseFactory;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.StatusVenda.*;
import static com.javacar.lojadecarro.support.TestConstants.ID_VALIDO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public final class VendaHelper extends BaseHelper {
    public static VendaRequest criarVendaRequest() {
        return VendaRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Venda criarVendaEntity() {
        return VendaEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static VendaResponse criarVendaResponse() {
        return VendaResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static VendaRequest criarVendasComCampos(Long idVeiculo) {
        return VendaRequestFactory
                .criarRequest()
                .comVeiculoPorId(idVeiculo)
                .build();
    }

    public static void assertVendaResponse(VendaResponse response) {
        assertThat(response)
                .isNotNull()
                .extracting(
                        VendaResponse::id,
                        VendaResponse::valorVenda,
                        VendaResponse::statusVenda
                ).containsExactly(
                        ID_VALIDO,
                        BigDecimal.valueOf(200000),
                        EM_ANDAMENTO
                );
    }

    public static void assertListVenda(Page<VendaResponse> response) {
        assertThat(response)
                .isNotNull()
                .extracting(
                        VendaResponse::id,
                        VendaResponse::valorVenda,
                        VendaResponse::statusVenda
                ).containsExactly(
                        tuple(1L, BigDecimal.valueOf(200000), CONCLUIDA),
                        tuple(2L, BigDecimal.valueOf(300000), PAUSADA),
                        tuple(3L, BigDecimal.valueOf(400000), CANCELADA),
                        tuple(4L, BigDecimal.valueOf(500000), EM_ANDAMENTO)
                );
    }

    public static void assertListVendaComStatus(Page<VendaResponse> response, StatusVenda status) {
        assertThat(response)
                .isNotNull()
                .extracting(
                        VendaResponse::id,
                        VendaResponse::valorVenda,
                        VendaResponse::statusVenda
                ).containsExactly(
                        tuple(1L, BigDecimal.valueOf(200000), status),
                        tuple(2L, BigDecimal.valueOf(300000), status)
                );
    }

    public static void assertVendaResponse(ResultActions result,
                                           ResultMatcher status) throws Exception {
        result.andExpect(status)
                .andExpect(jsonPath("$.id").value(ID_VALIDO))
                .andExpect(jsonPath("$.valorVenda").value(BigDecimal.valueOf(200000)))
                .andExpect(jsonPath("$.statusVenda").value(EM_ANDAMENTO.name()));
    }

    public static void assertPage(ResultActions result,
                                  ResultMatcher status) throws Exception {
        result.andExpect(status)
                .andExpect(jsonPath("$.content.length()").value(4))
                .andExpect(jsonPath("$.content[0].id").value(ID_VALIDO))
                .andExpect(jsonPath("$.content[0].valorVenda").value(BigDecimal.valueOf(200000)))
                .andExpect(jsonPath("$.content[0].statusVenda").value(CONCLUIDA.name()))
                .andExpect(jsonPath("$.content[1].id").value(2L))
                .andExpect(jsonPath("$.content[1].valorVenda").value(BigDecimal.valueOf(300000)))
                .andExpect(jsonPath("$.content[1].statusVenda").value(PAUSADA.name()))
                .andExpect(jsonPath("$.content[2].id").value(3L))
                .andExpect(jsonPath("$.content[2].valorVenda").value(BigDecimal.valueOf(400000)))
                .andExpect(jsonPath("$.content[2].statusVenda").value(CANCELADA.name()))
                .andExpect(jsonPath("$.content[3].id").value(4L))
                .andExpect(jsonPath("$.content[3].valorVenda").value(BigDecimal.valueOf(500000)))
                .andExpect(jsonPath("$.content[3].statusVenda").value(EM_ANDAMENTO.name()))
        ;
    }
}
