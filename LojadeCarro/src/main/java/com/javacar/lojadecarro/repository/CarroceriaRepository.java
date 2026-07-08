package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Carroceria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarroceriaRepository extends JpaRepository<Carroceria, Long> {
}
