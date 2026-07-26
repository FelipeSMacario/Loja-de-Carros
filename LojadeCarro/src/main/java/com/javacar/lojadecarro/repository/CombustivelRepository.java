package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
    List<Combustivel> findByAtivo(boolean b);
}
