package com.mercanoval.categorias.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CategoriaDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;
}