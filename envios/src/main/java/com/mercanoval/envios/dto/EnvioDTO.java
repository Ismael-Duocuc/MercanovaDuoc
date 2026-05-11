package com.mercanoval.envios.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EnvioDTO {

    @NotNull(message = "El pedido no puede estar vacío")
    private Long pedidoId;

    @NotBlank(message = "La dirección destino no puede estar vacía")
    private String direccionDestino;

    @NotBlank(message = "El estado no puede estar vacío")
    private String estado;

    @NotBlank(message = "El transportista no puede estar vacío")
    private String transportista;
}