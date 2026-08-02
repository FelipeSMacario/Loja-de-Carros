package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByAtivo(boolean ativo);

    boolean existsByEmail(String mail);

    boolean existsByCpf(String number);

    Optional<Usuario> findByCpf(String cpf);
}
