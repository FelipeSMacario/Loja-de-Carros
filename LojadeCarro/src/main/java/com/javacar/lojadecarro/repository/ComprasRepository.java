package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Compras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface ComprasRepository extends JpaRepository<Compras, Long> {

    @Modifying
    @Transactional
    @Query(value = " UPDATE lojadecarro.carro SET ativo = 0 WHERE id = :idCarro", nativeQuery = true)
    void marcaVendido(@Param("idCarro") Long idCarro);
}
