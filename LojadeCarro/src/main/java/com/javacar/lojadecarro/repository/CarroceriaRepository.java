package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Carroceria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarroceriaRepository extends JpaRepository<Carroceria, Long> {
    List<Carroceria> findByAtivo(boolean status);

    Optional<Carroceria> findByIdAndAtivo(Long id, Boolean ativo);
}
