package com.mercanoval.descuentos.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "descuentos")
public class Descuento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double porcentaje;

    @Column(nullable = false)
    private Boolean activo;
}