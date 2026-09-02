package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.enums.StatusVenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendasRepository extends JpaRepository<Venda, Long> {
    boolean existsByVeiculoIdAndStatusVenda(Long idVeiculo, StatusVenda statusVenda);

    Page<Venda> findByStatusVenda(StatusVenda statusVenda, Pageable pageable);

    @Query("""
            SELECT COUNT(v) > 0
            FROM Venda v
            WHERE v.id = :idVenda
              AND (
                  v.vendedor.id = :idUsuario
                  OR v.comprador.id = :idUsuario
              )
            """)
    boolean usuarioRelacionadoAVenda(
            @Param("idVenda") Long idVenda,
            @Param("idUsuario") Long idUsuario
    );

    boolean existsByVendedor_IdAndStatusVenda(Long idVendedor, StatusVenda statusVenda);

    boolean existsByComprador_IdAndStatusVenda(Long idUsuario, StatusVenda statusVenda
    );

    Page<Venda> findByVendedor_Id(Long idVendedor, Pageable pageable);

    Page<Venda> findByComprador_Id(Long idComprador, Pageable pageable);

    Page<Venda> findByVendedor_IdAndStatusVenda(Long idVendedor, Pageable pageable, StatusVenda statusVenda);

    Page<Venda> findByComprador_IdAndStatusVenda(Long idComprador, Pageable pageable, StatusVenda statusVenda);

    boolean existsByIdAndVendedor_Id(Long idVenda, Long idUsuario);
}
