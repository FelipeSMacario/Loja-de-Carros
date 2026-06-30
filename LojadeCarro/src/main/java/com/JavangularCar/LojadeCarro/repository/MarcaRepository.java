package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {


    public List<Marca> findByOrderByNomeAsc();

}
