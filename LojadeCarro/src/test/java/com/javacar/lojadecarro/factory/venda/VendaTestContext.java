package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;

import java.util.List;

import static com.javacar.lojadecarro.factory.helper.UsuarioHelper.criarUsuarioEntity;
import static com.javacar.lojadecarro.factory.helper.VeiculoHelper.criarVeiculoEntity;
import static com.javacar.lojadecarro.factory.helper.VendaHelper.*;

public class VendaTestContext {
    public final VendaRequest vendaRequest = criarVendaRequest();
    public final Venda vendaEntity = criarVendaEntity();
    public final Venda vendaEntity2 = VendaEntityFactory
            .criarEntity()
            .comTodosOsCampos()
            .comId(2L)
            .build();
    public final VendaResponse vendaResponse = criarVendaResponse();
    public final VendaResponse vendaResponse2 = VendaResponseFactory
            .criarResponse()
            .comTodosOsCampos()
            .comId(2L)
            .build();
    public final Usuario vendedor = criarUsuarioEntity();
    public final Veiculo veiculo = criarVeiculoEntity();
    public final Usuario comprador = UsuarioEntityFactory.criarEntity()
            .comTodosOsCampos()
            .comId(2L)
            .comNome("Goku")
            .comAtivo(true)
            .build();
    public final List<Venda> vendaEntityList = List.of(vendaEntity, vendaEntity2);

}
