package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Cor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoresRepository extends JpaRepository<Cor, Long> {
    List<Cor> findByAtivo(boolean b);

    Optional<Cor> findByNome(String nome);

    boolean existsByNome(String nome);
}
