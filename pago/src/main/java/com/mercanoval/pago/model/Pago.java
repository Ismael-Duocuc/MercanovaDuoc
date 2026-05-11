package com.mercanoval.pago.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "pagos")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private String metodoPago;

    @Column(nullable = false)
    private String estado;

    @Column
    private String codigoDescuento;

    @Column
    private Double descuentoAplicado;
}