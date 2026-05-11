package com.mercanoval.envios.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private String direccionDestino;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String transportista;
}