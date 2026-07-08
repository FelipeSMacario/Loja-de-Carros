package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.entity.Combustivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
}
