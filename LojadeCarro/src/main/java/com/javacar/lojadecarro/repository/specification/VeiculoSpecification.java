package com.javacar.lojadecarro.repository.specification;

import com.javacar.lojadecarro.dto.request.VeiculoFiltro;
import com.javacar.lojadecarro.entity.Veiculo;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.javacar.lojadecarro.enums.StatusVeiculo.DISPONIVEL;

public final class VeiculoSpecification {

    private VeiculoSpecification() {}

    public static Specification<Veiculo> comFiltro(VeiculoFiltro filtro) {
        Objects.requireNonNull(filtro,"Filtro não pode ser nulo.");

        List<Specification<Veiculo>> criterios = new ArrayList<>();

        criterios.add(statusDisponivel());

        if (filtro.marcaId() != null) {
            criterios.add(marcaIgual(filtro.marcaId()));
        }

        if (filtro.modeloId() != null) {
            criterios.add(modeloIgual(filtro.modeloId()));
        }

        if (filtro.carroceriaId() != null) {
            criterios.add(carroceriaIgual(filtro.carroceriaId()));
        }

        if (filtro.corId() != null) {
            criterios.add(corIgual(filtro.corId()));
        }

        if (filtro.combustivelId() != null) {
            criterios.add(combustivelIgual(filtro.combustivelId()));
        }

        if (filtro.anoMin() != null) {
            criterios.add(anoMaiorOuIgual(filtro.anoMin()));
        }

        if (filtro.anoMax() != null) {
            criterios.add(anoMenorOuIgual(filtro.anoMax()));
        }

        if (filtro.valorMin() != null) {
            criterios.add(valorMaiorOuIgual(filtro.valorMin()));
        }

        if (filtro.valorMax() != null) {
            criterios.add(valorMenorOuIgual(filtro.valorMax()));
        }

        if (filtro.quilometragemMax() != null) {
            criterios.add(quilometragemMenorOuIgual(filtro.quilometragemMax()));
        }

        return Specification.allOf(criterios);
    }

    private static Specification<Veiculo> statusDisponivel() {
        return (root, query, builder) ->
                builder.equal(
                        root.get("statusVeiculo"),
                        DISPONIVEL
                );
    }

    private static Specification<Veiculo> marcaIgual(
            Long marcaId
    ) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("modelo")
                                .get("marca")
                                .get("id"),
                        marcaId
                );
    }

    private static Specification<Veiculo> modeloIgual(
            Long modeloId
    ) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("modelo").get("id"),
                        modeloId
                );
    }

    private static Specification<Veiculo> carroceriaIgual(
            Long carroceriaId
    ) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("carroceria").get("id"),
                        carroceriaId
                );
    }

    private static Specification<Veiculo> corIgual(
            Long corId
    ) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("cor").get("id"),
                        corId
                );
    }

    private static Specification<Veiculo> combustivelIgual(
            Long combustivelId
    ) {
        return (root, query, builder) ->
                builder.equal(
                        root.get("combustivel").get("id"),
                        combustivelId
                );
    }

    private static Specification<Veiculo> anoMaiorOuIgual(
            Short anoMin
    ) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("anoFabricacao"),
                        anoMin
                );
    }

    private static Specification<Veiculo> anoMenorOuIgual(
            Short anoMax
    ) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("anoFabricacao"),
                        anoMax
                );
    }

    private static Specification<Veiculo> valorMaiorOuIgual(
            java.math.BigDecimal valorMin
    ) {
        return (root, query, builder) ->
                builder.greaterThanOrEqualTo(
                        root.get("valor"),
                        valorMin
                );
    }

    private static Specification<Veiculo> valorMenorOuIgual(
            java.math.BigDecimal valorMax
    ) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("valor"),
                        valorMax
                );
    }

    private static Specification<Veiculo>
    quilometragemMenorOuIgual(Integer quilometragemMax) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(
                        root.get("quilometragem"),
                        quilometragemMax
                );
    }
}