package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Opcional;
import com.javacar.lojadecarro.mapper.UsuarioMapper;
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

    Optional<Opcional> findByIdAndAtivoTrue(Long id);

    List<Opcional> findAllByIdInAndAtivoTrue(List<Long> ids);
}
