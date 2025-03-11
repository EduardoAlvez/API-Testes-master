package com.sistemaPedidos.pedidos.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Cidade {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "nome", nullable = false)
    private String nome;

    @ManyToOne
    @JoinColumn(name = "estado_id",nullable = false)
    private Estado estado;
}
