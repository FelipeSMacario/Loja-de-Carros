package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Imagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ImagensRepository extends JpaRepository<Imagem, Long> {


    Optional<Imagem> findByIdAndVeiculoId(Long idImagem, Long idCarro);

    Optional<List<Imagem>> findByVeiculoId(Long idCarro);


    @Modifying(clearAutomatically = true)
    @Query("""
            update Imagem i
               set i.principal = false
             where i.veiculo.id = :idVeiculo""")
    void desmarcarPrincipal(Long idVeiculo);
}
