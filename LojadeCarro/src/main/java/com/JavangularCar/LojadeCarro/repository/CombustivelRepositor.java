package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombustivelRepositor extends JpaRepository<Combustivel, Long> {
}
