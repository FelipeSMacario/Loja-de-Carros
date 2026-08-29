package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.VendaRequest;
import com.javacar.lojadecarro.dto.response.VendaResponse;
import com.javacar.lojadecarro.entity.Venda;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VendasMapper {

    Venda toEntity(VendaRequest request);

    @Mapping(source = "venda.veiculo.id", target = "veiculo.id")
    @Mapping(source = "venda.veiculo.modelo.marca.nome", target = "veiculo.marca")
    @Mapping(source = "venda.veiculo.modelo.nome", target = "veiculo.modelo")
    @Mapping(source = "venda.vendedor.id", target = "vendedor.id")
    @Mapping(source = "venda.vendedor.nome", target = "vendedor.nome")
    @Mapping(source = "venda.comprador.id", target = "comprador.id")
    @Mapping(source = "venda.comprador.nome", target = "comprador.nome")
    @Mapping(source = "venda.veiculo.statusVeiculo", target = "veiculo.status")
    VendaResponse toResponse(Venda venda);
}
