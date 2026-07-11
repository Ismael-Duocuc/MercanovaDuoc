package com.mercanoval.clientes.controller;

import com.mercanoval.clientes.dto.ClienteDTO;
import com.mercanoval.clientes.model.Cliente;
import com.mercanoval.clientes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Gestión de clientes de la plataforma Mercanova")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    @Operation(summary = "Listar todos los clientes",
            description = "Retorna la lista completa de clientes registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Cliente.class)))
    })
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        logger.info("GET /api/clientes");
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    @Operation(summary = "Obtener cliente por ID",
            description = "Busca y retorna un cliente específico según su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = Cliente.class))),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(
            @Parameter(description = "ID del cliente a buscar", example = "1") @PathVariable Long id) {
        logger.info("GET /api/clientes/{}", id);
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un nuevo cliente",
            description = "Registra un nuevo cliente en el sistema, validando que nombre, email, teléfono y dirección estén completos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"nombre\":\"Cliente Prueba\",\"email\":\"prueba@test.cl\",\"telefono\":\"+56911111111\",\"direccion\":\"Av. Siempre Viva 742\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos en el body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody ClienteDTO dto) {
        logger.info("POST /api/clientes");
        return ResponseEntity.status(201).body(clienteService.crear(dto));
    }

    @Operation(summary = "Actualizar un cliente existente",
            description = "Actualiza los datos de un cliente ya registrado según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Cliente.class))),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(
            @Parameter(description = "ID del cliente a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ClienteDTO dto) {
        logger.info("PUT /api/clientes/{}", id);
        return clienteService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un cliente",
            description = "Elimina un cliente existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un cliente con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del cliente a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/clientes/{}", id);
        return clienteService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
