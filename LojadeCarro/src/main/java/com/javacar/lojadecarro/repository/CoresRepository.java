package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Cores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoresRepository extends JpaRepository<Cores, Long> {
}
