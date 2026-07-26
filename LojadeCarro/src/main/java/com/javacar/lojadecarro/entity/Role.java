package com.javacar.lojadecarro.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Role extends EntidadeBase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;
}
