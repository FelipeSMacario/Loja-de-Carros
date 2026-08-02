package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.VeiculoOpcionaisRequest;
import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.ImagemResponse;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.*;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import com.javacar.lojadecarro.factory.imagem.ImagemResponseFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoRequestFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoResponseFactory;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.javacar.lojadecarro.factory.helper.CarroceriaHelper.criarCarroceriaEntity;
import static com.javacar.lojadecarro.factory.helper.CombustivelHelper.criarCombustivelEntity;
import static com.javacar.lojadecarro.factory.helper.CorHelper.criarCorEntity;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.criarImagemFile;
import static com.javacar.lojadecarro.factory.helper.ImagemHelper.criarListaImagem;
import static com.javacar.lojadecarro.factory.helper.ModeloHelper.criarModeloEntity;
import static com.javacar.lojadecarro.factory.helper.OpcionalHelper.criarListaOpcionals;
import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioEntity;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.*;

public class VeiculoTestContext {
    public final VeiculoRequest request = criarVeiculoRequest();
    public final VeiculoRequest requestIncompleto = VeiculoRequestFactory.veiculoRequestFactory().build();
    public final Veiculo entity = criarVeiculoEntity();
    public final VeiculoResponse response = criarVeiculoResponse();
    public final Carroceria carroceria = criarCarroceriaEntity();
    public final Cor cor = criarCorEntity();
    public final Modelo modelo = criarModeloEntity();
    public final Usuario usuario = criarUsuarioEntity();
    public final Combustivel combustivel = criarCombustivelEntity();
    public final List<Opcional> opcionais = criarListaOpcionals();
    public final List<Imagem> imagens = criarListaImagem();
    public final MultipartFile[] imagemFile = criarImagemFile();
    public final List<Long> idsOpcionais = List.of(1L, 2L);
    public final VeiculoOpcionaisRequest veiculoOpcionaisRequest = new VeiculoOpcionaisRequest(idsOpcionais);
    public final VeiculoOpcionaisRequest veiculoOpcionaisRequestIncompleto = new VeiculoOpcionaisRequest(null);
    public final List<ImagemResponse> imagemResponseList = List.of(ImagemResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .build(),
            ImagemResponseFactory
                    .criarResponse()
                    .comTodosOsCampos()
                    .comId(2L)
                    .build());

    public final VeiculoResponse veiculoResponse1 = VeiculoResponseFactory
            .criarResponse()
            .comTodosOsCampos()
            .comStatus(StatusVeiculo.PAUSADO)
            .comId(1L)
            .build();

    public final VeiculoResponse veiculoResponse2 = VeiculoResponseFactory
            .criarResponse()
            .comTodosOsCampos()
            .comStatus(StatusVeiculo.PAUSADO)
            .comId(2L)
            .build();

    public static VeiculoRequest criarVeiculoValido(){
        return VeiculoRequestFactory
                .criarRequest()
                .comQuilometragem(25000)
                .comValor(new BigDecimal(40000))
                .comPlaca("ABC4141")
                .comMotor("1.4 turbo")
                .comDescricao("Prestes a explodir")
                .comAnoFabricacao((short) 1998)
                .comIdCarroceria(1L)
                .comIdCores(2L)
                .comIdModelo(10L)
                .comIdUsuario(1L)
                .comIdCombustivel(1L)
                .comOpcionais(List.of(1L, 5L, 8L))
                .build();
    }

    public static VeiculoRequest criarVeiculoAtualizacaoValido(){
        return VeiculoRequestFactory
                .criarRequest()
                .comQuilometragem(25000)
                .comValor(new BigDecimal(40000))
                .comPlaca("HIJ7K89")
                .comMotor("1.4 turbo")
                .comDescricao("Não vai mais explodir")
                .comAnoFabricacao((short) 1998)
                .comIdCarroceria(1L)
                .comIdCores(2L)
                .comIdModelo(10L)
                .comIdUsuario(1L)
                .comIdCombustivel(1L)
                .comOpcionais(List.of(1L, 5L, 8L))
                .build();
    }

}
