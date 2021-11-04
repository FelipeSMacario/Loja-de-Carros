package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Kit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KitRepository extends JpaRepository<Kit, Long> {

    @Query(value = "SELECT * FROM lojadecarro.kit WHERE CARRO_ID = (SELECT ID FROM lojadecarro.carro WHERE ID = :valor); ", nativeQuery = true)
    Kit filtrarKit(@Param("valor") Long valor);


}
