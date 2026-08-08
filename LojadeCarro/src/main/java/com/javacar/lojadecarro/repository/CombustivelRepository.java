package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.dto.response.CombustivelResponse;
import com.javacar.lojadecarro.entity.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
    List<Combustivel> findByAtivo(boolean b);

    Optional<Combustivel> findByNome(String nome);

    boolean existsByNome(String nome);

    Optional<Combustivel> findByIdAndAtivoTrue(Long id);
}
