package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;

import java.math.BigDecimal;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;
import static org.assertj.core.api.Assertions.assertThat;

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
                        "QUV1F836",
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
}
