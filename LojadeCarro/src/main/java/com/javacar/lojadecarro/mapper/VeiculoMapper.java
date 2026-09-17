package com.javacar.lojadecarro.mapper;

import com.javacar.lojadecarro.dto.request.VeiculoRequest;
import com.javacar.lojadecarro.dto.response.*;
import com.javacar.lojadecarro.entity.Imagem;
import com.javacar.lojadecarro.entity.Usuario;
import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.entity.VeiculoOpcional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VeiculoMapper {

    Veiculo toEntity(VeiculoRequest request);

    @Mapping(
            source = "veiculo.modelo.marca.nome",
            target = "marca"
    )
    @Mapping(
            source = "veiculo.modelo.nome",
            target = "modelo"
    )
    @Mapping(
            source = "veiculo.carroceria.nome",
            target = "carroceria"
    )
    @Mapping(
            source = "veiculo.cor.nome",
            target = "cor"
    )
    @Mapping(
            source = "veiculo.combustivel.nome",
            target = "combustivel"
    )
    @Mapping(
            source = "imagemPrincipalId",
            target = "imagemPrincipalId"
    )
    VeiculoResponse toResponse(
            Veiculo veiculo,
            Long imagemPrincipalId
    );

    @Mapping(
            source = "modelo.marca.nome",
            target = "marca"
    )
    @Mapping(
            source = "modelo.nome",
            target = "modelo"
    )
    @Mapping(
            source = "carroceria.nome",
            target = "carroceria"
    )
    @Mapping(
            source = "cor.nome",
            target = "cor"
    )
    @Mapping(
            source = "combustivel.nome",
            target = "combustivel"
    )
    VeiculoDetalheResponse toDetalheResponse(
            Veiculo veiculo
    );

    VeiculoImagemResponse toImagemResponse(Imagem imagem);

    VendedorResumoResponse toVendedorResumo(
            Usuario vendedor
    );

    @Mapping(source = "opcional.id", target = "id")
    @Mapping(source = "opcional.nome", target = "nome")
    @Mapping(source = "opcional.ativo", target = "ativo")
    OpcionalResponse toOpcionalResponse(
            VeiculoOpcional veiculoOpcional
    );

    void toUpdate(
            VeiculoRequest request,
            @MappingTarget Veiculo veiculo
    );

    @Mapping(
            source = "carroceria.id",
            target = "idCarroceria"
    )
    @Mapping(
            source = "cor.id",
            target = "idCor"
    )
    @Mapping(
            source = "modelo.id",
            target = "idModelo"
    )
    @Mapping(
            source = "combustivel.id",
            target = "idCombustivel"
    )
    @Mapping(
            target = "idsOpcionais",
            expression = """
                    java(
                        veiculo.getOpcionais()
                            .stream()
                            .map(veiculoOpcional ->
                                veiculoOpcional
                                    .getOpcional()
                                    .getId()
                            )
                            .toList()
                    )
                    """
    )
    VeiculoEdicaoResponse toEdicaoResponse(
            Veiculo veiculo
    );
}