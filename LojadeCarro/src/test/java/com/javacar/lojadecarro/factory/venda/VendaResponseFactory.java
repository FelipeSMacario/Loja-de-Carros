package com.javacar.lojadecarro.factory.venda;

import com.javacar.lojadecarro.dto.response.VendaResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.javacar.lojadecarro.utils.Utils.ZONE;

public class VendaResponseFactory {
    private Long id;
    private BigDecimal valor;
    private LocalDateTime dataVenda;

    private VendaResponseFactory() {
    }

    public static VendaResponseFactory criarResponse() {
        return new VendaResponseFactory();
    }


    public VendaResponseFactory comTodosOsCampos() {
        this.id = 1L;
        this.valor = BigDecimal.valueOf(200000);
        this.dataVenda = (LocalDateTime.now(ZONE));
        return this;
    }
    public VendaResponseFactory comId(Long id) {
        this.id = id;
        return this;
    }


    public VendaResponse build() {
        return new VendaResponse(id, valor, dataVenda);
    }
}
