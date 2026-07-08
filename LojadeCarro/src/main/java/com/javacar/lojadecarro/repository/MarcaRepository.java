package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {


    public List<Marca> findByOrderByNomeAsc();

}
