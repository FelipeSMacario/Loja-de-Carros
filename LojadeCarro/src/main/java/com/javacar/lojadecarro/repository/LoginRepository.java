package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String login);
}
