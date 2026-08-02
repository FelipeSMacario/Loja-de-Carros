package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Venda;
import com.javacar.lojadecarro.enums.StatusVenda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendasRepository extends JpaRepository<Venda, Long> {
    boolean existsByVeiculoId(Long idVeiculo);

    Page<Venda> findByStatusVenda(StatusVenda statusVenda, Pageable pageable);
}
