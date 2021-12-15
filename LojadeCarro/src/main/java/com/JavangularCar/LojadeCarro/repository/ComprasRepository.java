package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Compras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface ComprasRepository extends JpaRepository<Compras, Long> {

    @Modifying
    @Transactional
    @Query(value = " UPDATE lojadecarro.carro SET ativo = 0 WHERE id = :idCarro", nativeQuery = true)
    void marcaVendido(@Param("idCarro") Long idCarro);
}
