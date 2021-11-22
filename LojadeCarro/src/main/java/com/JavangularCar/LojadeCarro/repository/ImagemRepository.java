package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Long> {
}
