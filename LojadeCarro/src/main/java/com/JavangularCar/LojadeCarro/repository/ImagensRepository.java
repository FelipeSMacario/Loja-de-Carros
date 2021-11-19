package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Imagens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface ImagensRepository extends JpaRepository<Imagens, Long> {
}
