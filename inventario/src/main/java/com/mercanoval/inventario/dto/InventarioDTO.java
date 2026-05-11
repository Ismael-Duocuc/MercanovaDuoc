package com.mercanoval.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InventarioDTO {

    @NotNull(message = "El producto no puede estar vacío")
    private Long productoId;

    @NotNull(message = "El stock no puede estar vacío")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El stock mínimo no puede estar vacío")
    @PositiveOrZero(message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    @NotBlank(message = "La ubicación no puede estar vacía")
    private String ubicacion;
}