package com.mercanoval.envios.controller;

import com.mercanoval.envios.dto.EnvioDTO;
import com.mercanoval.envios.model.Envio;
import com.mercanoval.envios.service.EnvioService;
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
@RequestMapping("/api/envios")
@RequiredArgsConstructor
@Tag(name = "Envíos", description = "Gestión de envíos; verifica pedido vía comunicación REST")
public class EnvioController {

    private static final Logger logger = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;

    @Operation(summary = "Listar todos los envíos", description = "Retorna la lista completa de envíos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de envíos obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Envio.class)))
    })
    @GetMapping
    public ResponseEntity<List<Envio>> obtenerTodos() {
        logger.info("GET /api/envios");
        return ResponseEntity.ok(envioService.obtenerTodos());
    }

    @Operation(summary = "Obtener envío por ID", description = "Busca y retorna un envío según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado",
                    content = @Content(schema = @Schema(implementation = Envio.class))),
            @ApiResponse(responseCode = "404", description = "No existe un envío con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(
            @Parameter(description = "ID del envío", example = "1") @PathVariable Long id) {
        logger.info("GET /api/envios/{}", id);
        return envioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar envíos por pedido", description = "Retorna todos los envíos asociados a un pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de envíos del pedido",
                    content = @Content(schema = @Schema(implementation = Envio.class)))
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Envio>> obtenerPorPedido(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long pedidoId) {
        logger.info("GET /api/envios/pedido/{}", pedidoId);
        return ResponseEntity.ok(envioService.obtenerPorPedido(pedidoId));
    }

    @Operation(summary = "Buscar envíos por estado", description = "Retorna todos los envíos que tienen el estado indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de envíos con ese estado",
                    content = @Content(schema = @Schema(implementation = Envio.class)))
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Envio>> obtenerPorEstado(
            @Parameter(description = "Estado del envío", example = "PENDIENTE") @PathVariable String estado) {
        logger.info("GET /api/envios/estado/{}", estado);
        return ResponseEntity.ok(envioService.obtenerPorEstado(estado));
    }

    @Operation(summary = "Crear un nuevo envío",
            description = "Crea un envío verificando previamente, vía WebClient, que el pedido asociado exista en el microservicio de pedidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Envío creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Envio.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"pedidoId\":20,\"direccionDestino\":\"Av. Kennedy 5000\",\"estado\":\"PENDIENTE\",\"transportista\":\"Chilexpress\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, o el pedido asociado no existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Envio> crear(@Valid @RequestBody EnvioDTO dto) {
        logger.info("POST /api/envios");
        try {
            return ResponseEntity.status(201).body(envioService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear envío: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar un envío existente", description = "Actualiza los datos de un envío ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Envio.class))),
            @ApiResponse(responseCode = "404", description = "No existe un envío con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizar(
            @Parameter(description = "ID del envío a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody EnvioDTO dto) {
        logger.info("PUT /api/envios/{}", id);
        return envioService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un envío", description = "Elimina un envío existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Envío eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un envío con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del envío a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/envios/{}", id);
        return envioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
