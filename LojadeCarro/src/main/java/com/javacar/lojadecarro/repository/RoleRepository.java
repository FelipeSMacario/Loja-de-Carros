package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByAtivo(boolean b);

    List<Role> findAllByIdIn(List<Long> requests);

    Optional<Role> findByNome(String nome);
}
