package com.JavangularCar.LojadeCarro.repository;

import com.JavangularCar.LojadeCarro.model.Imagens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public interface ImagensRepository extends JpaRepository<Imagens, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE lojadecarro.imagens SET carro_id = :idCarro WHERE id = :idImagem", nativeQuery = true)
    void updateEstoque(@Param("idCarro") Long idCarro, @Param("idImagem") Long idImagem);

    @Modifying
    @Transactional
    @Query(value = "UPDATE lojadecarro.carro SET url = :urlDefault WHERE id = :idCarro", nativeQuery = true)
    void imagemCarroDefault(@Param("urlDefault") String urlDefault, @Param("idCarro") Long idCarro);

}
