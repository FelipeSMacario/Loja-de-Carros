package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarroRepository  extends JpaRepository<Carro, Long> {


    @Query(value ="SELECT * FROM lojadecarro.carro WHERE marca_id IN (SELECT id FROM lojadecarro.marca WHERE nome Like %:Marca%);", nativeQuery = true)
    List<Carro> findByMarca(@Param("Marca") String Marca);
}
