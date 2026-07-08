package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.dto.request.FiltrarCamposCarroRequest;
import com.javacar.lojadecarro.entity.Carro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {

    @Query(value = "SELECT * FROM lojadecarro.carro " +
            "WHERE marca_id IN (SELECT id FROM lojadecarro.marca WHERE nome = IFNULL(:#{#filtro.marca}, nome)) " +
            "AND modelo_id IN (SELECT id FROM lojadecarro.modelo WHERE nome = IFNULL(:#{#filtro.modelo}, nome)) " +
            "AND (ano_fabricacao BETWEEN IFNULL(:#{#filtro.anoInicio}, ano_fabricacao) AND IFNULL(:#{#filtro.anoFim}, ano_fabricacao)) " +
            "AND (valor BETWEEN IFNULL(:#{#filtro.valorInicio}, valor) AND IFNULL(:#{#filtro.valorFim}, valor)) " +
            "AND (quilometragem <= IFNULL(:#{#filtro.quilometragem}, quilometragem) AND ATIVO = 1)",
            countQuery = "SELECT count(*) FROM lojadecarro.carro " +
                    "WHERE marca_id IN (SELECT id FROM lojadecarro.marca WHERE nome = IFNULL(:#{#filtro.marca}, nome)) " +
                    "AND modelo_id IN (SELECT id FROM lojadecarro.modelo WHERE nome = IFNULL(:#{#filtro.modelo}, nome)) " +
                    "AND (ano_fabricacao BETWEEN IFNULL(:#{#filtro.anoInicio}, ano_fabricacao) AND IFNULL(:#{#filtro.anoFim}, ano_fabricacao)) " +
                    "AND (valor BETWEEN IFNULL(:#{#filtro.valorInicio}, valor) AND IFNULL(:#{#filtro.valorFim}, valor)) " +
                    "AND (quilometragem <= IFNULL(:#{#filtro.quilometragem}, quilometragem) AND ATIVO = 1)",
            nativeQuery = true)
    Page<Carro> findByCampos(@Param("filtro") FiltrarCamposCarroRequest filtro, Pageable pageable);



}
