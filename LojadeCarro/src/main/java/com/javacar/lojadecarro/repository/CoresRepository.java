package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Cor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoresRepository extends JpaRepository<Cor, Long> {
    List<Cor> findByAtivo(boolean b);
}
