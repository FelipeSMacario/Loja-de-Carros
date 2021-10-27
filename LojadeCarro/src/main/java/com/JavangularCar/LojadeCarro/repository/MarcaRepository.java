package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {
}
