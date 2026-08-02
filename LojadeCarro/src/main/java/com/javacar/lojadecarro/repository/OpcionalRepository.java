package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Opcional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpcionalRepository extends JpaRepository<Opcional, Long> {

    List<Opcional> findByAtivo(boolean b);

    List<Opcional> findAllByIdIn(List<Long> ids);

    Optional<Opcional> findByNome(String nome);

    boolean existsByNome(String nome);
}
