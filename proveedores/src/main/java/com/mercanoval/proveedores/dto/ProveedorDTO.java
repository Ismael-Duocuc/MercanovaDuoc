package com.mercanoval.proveedores.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProveedorDTO {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    private String telefono;

    @NotBlank(message = "La dirección no puede estar vacía")
    private String direccion;

    @NotBlank(message = "El país no puede estar vacío")
    private String pais;
}