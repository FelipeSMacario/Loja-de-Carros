package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Opcional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpcionalRepository extends JpaRepository<Opcional, Long> {

    List<Opcional> findByAtivo(boolean b);

    List<Opcional> findAllByIdIn(List<Long> ids);
}
