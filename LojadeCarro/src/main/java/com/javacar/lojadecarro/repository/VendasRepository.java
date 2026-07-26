package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendasRepository extends JpaRepository<Venda, Long> {
}
