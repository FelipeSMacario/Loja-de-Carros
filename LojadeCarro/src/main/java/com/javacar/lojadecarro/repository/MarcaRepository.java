package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findByAtivo(boolean b);

    boolean existsByNome(String nome);

    boolean existsByUrl(String url);

    Optional<Marca> findByNome(String fiat);
    Optional<Marca> findByIdAndAtivoTrue(Long id);
}
