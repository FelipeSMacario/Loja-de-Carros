package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Carro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {

    @Query(value = "SELECT * FROM lojadecarro.carro WHERE marca_id IN (SELECT id FROM lojadecarro.marca WHERE nome = IFNULL(:marca,nome)) AND " +
            "modelo_id IN ( SELECT id FROM lojadecarro.modelo WHERE nome = IFNULL(:modelo,nome)) AND \n" +
            "(ano_fabricacao BETWEEN IFNULL(:anoInicio,ano_fabricacao) AND IFNULL(:anoFim,ano_fabricacao)) AND " +
            "(valor BETWEEN IFNULL(:valorInicio, valor) AND IFNULL(:valorFim, valor)) AND" +
            "(quilometragem <= IFNULL(:quilometragem,quilometragem))",
            countQuery = "SELECT count(*) FROM lojadecarro.carro WHERE marca_id IN (SELECT id FROM lojadecarro.marca WHERE nome = IFNULL(:marca,nome)) AND " +
                    "modelo_id IN ( SELECT id FROM lojadecarro.modelo WHERE nome = IFNULL(:modelo,nome)) AND \n" +
                    "(ano_fabricacao BETWEEN IFNULL(:anoInicio,ano_fabricacao) AND IFNULL(:anoFim,ano_fabricacao)) AND " +
                    "(valor BETWEEN IFNULL(:valorInicio, valor) AND IFNULL(:valorFim, valor)) AND" +
                    "(quilometragem <= IFNULL(:quilometragem,quilometragem))",
            nativeQuery = true)
    Page<Carro> FindByCampos(@Param("marca") String marca,
                             @Param("modelo") String modelo,
                             @Param("anoInicio") Integer anoInicio,
                             @Param("anoFim") Integer anoFim,
                             @Param("valorInicio") Double valorInicio,
                             @Param("valorFim") Double valorFim,
                             @Param("quilometragem") Double quilometragem,
                             Pageable pageable);


}
