package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImagensRepository extends JpaRepository<Imagem, Long> {


    Optional<Imagem> findByIdAndVeiculoId(Long idImagem, Long idCarro);

    List<Imagem> findByVeiculoId(Long idCarro);

    Optional<Imagem> findByBucketAndObjectKey(String bucket, String objectKey);
}
