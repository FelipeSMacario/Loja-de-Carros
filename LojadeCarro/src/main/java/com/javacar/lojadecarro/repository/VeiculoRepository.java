package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Veiculo;
import com.javacar.lojadecarro.enums.StatusVeiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Page<Veiculo> findByStatusVeiculo(StatusVeiculo statusVeiculo, Pageable pageable);
    Optional<Veiculo> findByPlaca(String placa);

    boolean existsByPlaca(String placa);
    boolean existsByIdAndVendedor_Id(Long idVeiculo, Long idVendedor);

    List<Veiculo> findByVendedor_IdAndStatusVeiculo(Long id, StatusVeiculo statusVeiculo);
    Page<Veiculo> findByVendedor_IdAndStatusVeiculo(Long id, StatusVeiculo statusVeiculo, Pageable pageable);
    boolean existsByVendedor_IdAndStatusVeiculo(Long idUsuario,  StatusVeiculo statusVeiculo);

    Page<Veiculo> findByVendedor_Id(
            Long idUsuario,
            Pageable pageable
    );

    Optional<Veiculo> findByIdAndStatusVeiculo(Long id, StatusVeiculo statusVeiculo);
}
