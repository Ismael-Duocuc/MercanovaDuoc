package com.mercanoval.pedidos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PedidoDTO {

    @NotNull(message = "El cliente no puede estar vacío")
    private Long clienteId;

    @NotNull(message = "El producto no puede estar vacío")
    private Long productoId;

    @NotNull(message = "La cantidad no puede estar vacía")
    @Positive(message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El total no puede estar vacío")
    @Positive(message = "El total debe ser mayor a 0")
    private Double total;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;
}