package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Carro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarroRepository extends JpaRepository<Carro, Long> {


    @Query(value = "SELECT * FROM lojadecarro.carro WHERE marca_id IN (SELECT id FROM lojadecarro.marca WHERE nome = :Marca );", nativeQuery = true)
    List<Carro> findByMarca(@Param("Marca") Optional<String> Marca);


    @Query(value = "SELECT * FROM lojadecarro.carro WHERE marca_id = (SELECT id FROM lojadecarro.marca WHERE nome = :Marca) AND modelo_id = ( SELECT id FROM lojadecarro.modelo WHERE nome = :Modelo);", nativeQuery = true)
    List<Carro> findByModelo(@Param("Marca") String Marca, @Param("Modelo") String Modelo);

    List<Carro> findByValorBetween(Double valor1, Double valor2);

    List<Carro> findByAnoFabricacaoBetween(int valor1, int valor2);

    List<Carro> findByQuilometragemLessThanEqual(Double valor);
}
