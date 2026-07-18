package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
    List<Modelo> findByAtivo(boolean b);
}
