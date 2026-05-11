package com.mercanoval.descuentos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DescuentoDTO {

    @NotBlank(message = "El código no puede estar vacío")
    private String codigo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El porcentaje no puede estar vacío")
    @Positive(message = "El porcentaje debe ser mayor a 0")
    @Max(value = 100, message = "El porcentaje no puede ser mayor a 100")
    private Double porcentaje;

    @NotNull(message = "El estado no puede estar vacío")
    private Boolean activo;
}