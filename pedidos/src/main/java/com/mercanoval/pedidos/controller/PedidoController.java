package com.mercanoval.pedidos.controller;

import com.mercanoval.pedidos.dto.PedidoDTO;
import com.mercanoval.pedidos.model.Pedido;
import com.mercanoval.pedidos.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de pedidos; verifica cliente y producto vía comunicación REST")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoService pedidoService;

    @Operation(summary = "Listar todos los pedidos", description = "Retorna la lista completa de pedidos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Pedido.class)))
    })
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodos() {
        logger.info("GET /api/pedidos");
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    @Operation(summary = "Obtener pedido por ID", description = "Busca y retorna un pedido según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long id) {
        logger.info("GET /api/pedidos/{}", id);
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar pedidos por cliente", description = "Retorna todos los pedidos realizados por un cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos del cliente",
                    content = @Content(schema = @Schema(implementation = Pedido.class)))
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> obtenerPorCliente(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long clienteId) {
        logger.info("GET /api/pedidos/cliente/{}", clienteId);
        return ResponseEntity.ok(pedidoService.obtenerPorCliente(clienteId));
    }

    @Operation(summary = "Buscar pedidos por estado", description = "Retorna todos los pedidos que tienen el estado indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pedidos con ese estado",
                    content = @Content(schema = @Schema(implementation = Pedido.class)))
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> obtenerPorEstado(
            @Parameter(description = "Estado del pedido", example = "PENDIENTE") @PathVariable String estado) {
        logger.info("GET /api/pedidos/estado/{}", estado);
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    @Operation(summary = "Crear un nuevo pedido",
            description = "Crea un pedido verificando previamente, vía WebClient, que el cliente y el producto existan en sus microservicios respectivos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Pedido.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"clienteId\":1,\"productoId\":1,\"cantidad\":2,\"total\":1199980.0,\"estado\":\"PENDIENTE\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, o el cliente/producto no existen", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoDTO dto) {
        logger.info("POST /api/pedidos");
        try {
            return ResponseEntity.status(201).body(pedidoService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear pedido: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar un pedido existente", description = "Actualiza los datos de un pedido ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Pedido.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(
            @Parameter(description = "ID del pedido a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody PedidoDTO dto) {
        logger.info("PUT /api/pedidos/{}", id);
        return pedidoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un pedido", description = "Elimina un pedido existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pedido eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un pedido con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del pedido a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/pedidos/{}", id);
        return pedidoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
