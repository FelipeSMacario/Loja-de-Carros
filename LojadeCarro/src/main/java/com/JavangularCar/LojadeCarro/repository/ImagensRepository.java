package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Carro;
import com.JavangularCar.LojadeCarro.model.Imagens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public interface ImagensRepository extends JpaRepository<Imagens, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE lojadecarro.imagens SET carro_id = :idCarro WHERE id = :idImagem", nativeQuery = true)
    void updateEstoque(@Param("idCarro") Long idCarro, @Param("idImagem") Long idImagem);
}
