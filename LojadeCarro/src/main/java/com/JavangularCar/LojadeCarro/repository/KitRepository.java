package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {
}
