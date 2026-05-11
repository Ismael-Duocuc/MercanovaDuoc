package com.mercanoval.pago.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PagoDTO {

    @NotNull(message = "El pedido no puede estar vacío")
    private Long pedidoId;

    @NotNull(message = "El monto no puede estar vacío")
    @Positive(message = "El monto debe ser mayor a 0")
    private Double monto;

    @NotBlank(message = "El método de pago no puede estar vacío")
    private String metodoPago;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    private String codigoDescuento;
}