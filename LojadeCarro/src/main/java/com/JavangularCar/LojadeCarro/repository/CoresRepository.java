package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Cores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoresRepository extends JpaRepository<Cores, Long> {
}
