package com.javacar.lojadecarro.factory.helper;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.VeiculoResponse;
import com.javacar.lojadecarro.entity.*;
import org.springframework.web.multipart.MultipartFile;

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
}
