package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.factory.usuario.UsuarioEntityFactory;
import com.javacar.lojadecarro.factory.veiculo.VeiculoEntityFactory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.utils.Utils.ZONE;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class VendaEntityFactory {

    private final Venda venda;

    private VendaEntityFactory() {
        this.venda = new Venda();
    }

    public static VendaEntityFactory criarEntity() {
        return new VendaEntityFactory();
    }

    public VendaEntityFactory comTodosOsCampos() {
        venda.setId(1L);
        venda.setVeiculo(VeiculoEntityFactory.criarEntity().comTodosOsCampos().build());
        venda.setVendedor(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        venda.setComprador(UsuarioEntityFactory.criarEntity().comTodosOsCampos().build());
        venda.setValorVenda(BigDecimal.valueOf(200000));
        venda.setDataVenda(LocalDateTime.now(ZONE));
        return this;
    }


    public Venda build() {
        return venda;
    }
}
