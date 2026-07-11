package com.mercanoval.carrito.controller;

import com.mercanoval.carrito.dto.CarritoDTO;
import com.mercanoval.carrito.model.Carrito;
import com.mercanoval.carrito.service.CarritoService;
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
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Gestión del carrito de compras; verifica cliente y producto vía comunicación REST")
public class CarritoController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);
    private final CarritoService carritoService;

    @Operation(summary = "Listar todos los items del carrito", description = "Retorna todos los items de carrito registrados en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de items obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Carrito.class)))
    })
    @GetMapping
    public ResponseEntity<List<Carrito>> obtenerTodos() {
        logger.info("GET /api/carrito");
        return ResponseEntity.ok(carritoService.obtenerTodos());
    }

    @Operation(summary = "Obtener item del carrito por ID", description = "Busca y retorna un item específico del carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item encontrado",
                    content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "No existe un item con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerPorId(
            @Parameter(description = "ID del item del carrito", example = "1") @PathVariable Long id) {
        logger.info("GET /api/carrito/{}", id);
        return carritoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener carrito de un cliente", description = "Retorna todos los items del carrito pertenecientes a un cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Items del carrito del cliente",
                    content = @Content(schema = @Schema(implementation = Carrito.class)))
    })
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Carrito>> obtenerPorCliente(
            @Parameter(description = "ID del cliente", example = "1") @PathVariable Long clienteId) {
        logger.info("GET /api/carrito/cliente/{}", clienteId);
        return ResponseEntity.ok(carritoService.obtenerPorCliente(clienteId));
    }

    @Operation(summary = "Agregar un item al carrito",
            description = "Agrega un producto al carrito de un cliente, verificando previamente vía WebClient que tanto el cliente como el producto existan.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item agregado exitosamente al carrito",
                    content = @Content(schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"clienteId\":1,\"productoId\":1,\"cantidad\":2,\"precioUnitario\":599990.0,\"total\":1199980.0}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, o el cliente/producto no existen", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Carrito> agregar(@Valid @RequestBody CarritoDTO dto) {
        logger.info("POST /api/carrito");
        try {
            return ResponseEntity.status(201).body(carritoService.agregar(dto));
        } catch (RuntimeException e) {
            logger.error("Error al agregar item al carrito: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar un item del carrito", description = "Actualiza cantidad, precio o total de un item ya existente en el carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "No existe un item con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizar(
            @Parameter(description = "ID del item a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody CarritoDTO dto) {
        logger.info("PUT /api/carrito/{}", id);
        return carritoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un item del carrito", description = "Elimina un item específico del carrito según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un item con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del item a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/carrito/{}", id);
        return carritoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Vaciar el carrito de un cliente", description = "Elimina todos los items del carrito pertenecientes a un cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Carrito vaciado correctamente", content = @Content)
    })
    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciarCarrito(
            @Parameter(description = "ID del cliente cuyo carrito se vaciará", example = "1") @PathVariable Long clienteId) {
        logger.info("DELETE /api/carrito/cliente/{}", clienteId);
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }
}
