package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.entity.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long> {
}
