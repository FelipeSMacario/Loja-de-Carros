package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public final class VeiculoHelper extends BaseHelper {
    public static VeiculoRequest criarVeiculoRequest() {
        return VeiculoRequestFactory
                .criarRequest()
                .comTodosOsCampos()
                .build();
    }

    public static Veiculo criarVeiculoEntity() {
        return VeiculoEntityFactory
                .criarEntity()
                .comTodosOsCampos()
                .build();
    }

    public static VeiculoResponse criarVeiculoResponse() {
        return VeiculoResponseFactory
                .criarResponse()
                .comTodosOsCampos()
                .build();
    }

    public static void assertVeiculoResponse(VeiculoResponse resultado) {
        assertThat(resultado)
                .isNotNull()
                .extracting(
                        VeiculoResponse::id,
                        VeiculoResponse::placa,
                        VeiculoResponse::marca,
                        VeiculoResponse::modelo,
                        VeiculoResponse::valor,
                        VeiculoResponse::quilometragem,
                        VeiculoResponse::anoFabricacao,
                        VeiculoResponse::statusVeiculo
                ).containsExactly(
                        1L,
                        "QUV1F83",
                        "Chevrolet",
                        "Onix",
                        new BigDecimal(58000),
                        67000,
                        (short) 2020,
                        DISPONIVEL
                );
    }

    public static void assertDependenciasVeiculoCompleto(VeiculoTestContext cx, StatusVeiculo statusVeiculo) {
        assertThat(cx.entity.getStatusVeiculo())
                .isEqualTo(statusVeiculo);

        assertThat(cx.entity.getCarroceria())
                .isNotNull()
                .isSameAs(cx.carroceria);

        assertThat(cx.entity.getCor())
                .isNotNull()
                .isSameAs(cx.cor);

        assertThat(cx.entity.getModelo())
                .isNotNull()
                .isSameAs(cx.modelo);

        assertThat(cx.entity.getVendedor())
                .isNotNull()
                .isSameAs(cx.usuario);

        assertThat(cx.entity.getCombustivel())
                .isNotNull()
                .isSameAs(cx.combustivel);

        assertThat(cx.entity.getOpcionais())
                .extracting(vo -> vo.getOpcional().getId())
                .containsExactlyElementsOf(
                        cx.opcionais.stream()
                                .map(Opcional::getId)
                                .toList()
                );

        assertThat(cx.entity.getImagens())
                .containsExactlyElementsOf(cx.imagens);

    }

    public static void assertDependenciasVeiculoCompletoSemImagemEOpcional(VeiculoTestContext cx) {

        assertThat(cx.entity.getCarroceria())
                .isNotNull()
                .isSameAs(cx.carroceria);

        assertThat(cx.entity.getCor())
                .isNotNull()
                .isSameAs(cx.cor);

        assertThat(cx.entity.getModelo())
                .isNotNull()
                .isSameAs(cx.modelo);

        assertThat(cx.entity.getVendedor())
                .isNotNull()
                .isSameAs(cx.usuario);

        assertThat(cx.entity.getCombustivel())
                .isNotNull()
                .isSameAs(cx.combustivel);

    }
    public static void assertVeiculo(ResultActions result,
                                     ResultMatcher status,
                                     Long id,
                                     String placa,
                                     String marca,
                                     String modelo,
                                     BigDecimal valor,
                                     Double quilometragem,
                                     short anoFabricacao,
                                     StatusVeiculo statusVeiculo) throws Exception {
        result
                .andExpect(status)
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.placa").value(placa))
                .andExpect(jsonPath("$.marca").value(marca))
                .andExpect(jsonPath("$.modelo").value(modelo))
                .andExpect(jsonPath("$.valor").value(valor))
                .andExpect(jsonPath("$.quilometragem").value(quilometragem))
                .andExpect(jsonPath("$.anoFabricacao").value((int) anoFabricacao))
                .andExpect(jsonPath("$.statusVeiculo").value(statusVeiculo.toString()));
    }

    public static void assertVeiculoList(ResultActions result,
                                         ResultMatcher status,
                                         Long primeiroId,
                                         Long segundoId,
                                         String primeiraPlaca,
                                         String segundaPlaca,
                                         String primeiraMarca,
                                         String segundaaMarca,
                                         String primeiroModelo,
                                         String segundoModelo,
                                         BigDecimal primeiroValor,
                                         BigDecimal segundoValor,
                                         Double primeiraQuilometragem,
                                         Double segundaQuilometragem,
                                         short primeiroAnoFabricacao,
                                         short segundoAnoFabricacao,
                                         StatusVeiculo primeiroStatusVeiculo,
                                         StatusVeiculo segundoStatusVeiculo
                                         ) throws Exception {
        result
                .andExpect(status)
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(primeiroId))
                .andExpect(jsonPath("$.content[1].id").value(segundoId))
                .andExpect(jsonPath("$.content[0].placa").value(primeiraPlaca))
                .andExpect(jsonPath("$.content[1].placa").value(segundaPlaca))
                .andExpect(jsonPath("$.content[0].marca").value(primeiraMarca))
                .andExpect(jsonPath("$.content[1].marca").value(segundaaMarca))
                .andExpect(jsonPath("$.content[0].modelo").value(primeiroModelo))
                .andExpect(jsonPath("$.content[1].modelo").value(segundoModelo))
                .andExpect(jsonPath("$.content[0].valor").value(primeiroValor))
                .andExpect(jsonPath("$.content[1].valor").value(segundoValor))
                .andExpect(jsonPath("$.content[0].quilometragem").value(primeiraQuilometragem))
                .andExpect(jsonPath("$.content[1].quilometragem").value(segundaQuilometragem))
                .andExpect(jsonPath("$.content[0].anoFabricacao").value((int) primeiroAnoFabricacao))
                .andExpect(jsonPath("$.content[1].anoFabricacao").value((int) segundoAnoFabricacao))
                .andExpect(jsonPath("$.content[0].statusVeiculo").value(primeiroStatusVeiculo.toString()))
                .andExpect(jsonPath("$.content[1].statusVeiculo").value(segundoStatusVeiculo.toString()));

    }
}
