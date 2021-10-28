package com.JavangularCar.LojadeCarro.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    private Long id;

    private String cpf;

    private LocalDateTime dtNascimento;

    private String nome;
}
