package com.mercanoval.productos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductoDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El precio no puede estar vacío")
    @Positive(message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotBlank(message = "La categoría no puede estar vacía")
    private String categoria;

    @NotBlank(message = "El proveedor no puede estar vacío")
    private String proveedor;
}