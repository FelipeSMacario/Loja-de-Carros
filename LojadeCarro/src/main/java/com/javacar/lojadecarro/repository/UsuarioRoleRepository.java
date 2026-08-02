package com.javacar.lojadecarro.repository;

import com.javacar.lojadecarro.entity.UsuarioRole;
import com.javacar.lojadecarro.entity.UsuarioRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, UsuarioRoleId> {
}
