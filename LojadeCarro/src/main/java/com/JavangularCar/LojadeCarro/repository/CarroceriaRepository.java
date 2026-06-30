package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.entity.Carroceria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarroceriaRepository extends JpaRepository<Carroceria, Long> {
}
