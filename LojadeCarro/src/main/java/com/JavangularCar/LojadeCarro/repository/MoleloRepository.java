package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoleloRepository extends JpaRepository<Modelo, Long> {
}
