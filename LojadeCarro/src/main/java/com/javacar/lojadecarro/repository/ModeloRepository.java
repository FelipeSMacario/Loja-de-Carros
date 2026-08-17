package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    List<Modelo> findByAtivo(boolean ativo);

    boolean existsByNome(String nome);

    Optional<Modelo> findByNome(String nome);

    Optional<Modelo> findByIdAndAtivoTrue(Long id);

    Optional<Modelo> findByIdAndAtivoTrueAndMarca_AtivoTrue(Long id);

    List<Modelo> findByAtivoTrueAndMarca_AtivoTrue();
}
