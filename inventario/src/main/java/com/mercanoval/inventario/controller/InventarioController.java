package com.mercanoval.inventario.controller;

import com.mercanoval.inventario.dto.InventarioDTO;
import com.mercanoval.inventario.model.Inventario;
import com.mercanoval.inventario.service.InventarioService;
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
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Gestión de stock; verifica producto vía comunicación REST")
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);
    private final InventarioService inventarioService;

    @Operation(summary = "Listar todo el inventario", description = "Retorna todos los registros de inventario existentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de inventario obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Inventario.class)))
    })
    @GetMapping
    public ResponseEntity<List<Inventario>> obtenerTodos() {
        logger.info("GET /api/inventario");
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    @Operation(summary = "Obtener inventario por ID", description = "Busca y retorna un registro de inventario según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario encontrado",
                    content = @Content(schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No existe un inventario con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerPorId(
            @Parameter(description = "ID del inventario", example = "1") @PathVariable Long id) {
        logger.info("GET /api/inventario/{}", id);
        return inventarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener inventario por producto", description = "Busca el registro de inventario asociado a un producto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario del producto encontrado",
                    content = @Content(schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No existe inventario para ese producto", content = @Content)
    })
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> obtenerPorProducto(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long productoId) {
        logger.info("GET /api/inventario/producto/{}", productoId);
        return inventarioService.obtenerPorProducto(productoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar inventarios con stock bajo", description = "Retorna los registros de inventario cuyo stock es igual o menor a 10 unidades.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de inventarios con stock bajo",
                    content = @Content(schema = @Schema(implementation = Inventario.class)))
    })
    @GetMapping("/stockbajo")
    public ResponseEntity<List<Inventario>> obtenerStockBajo() {
        logger.info("GET /api/inventario/stockbajo");
        return ResponseEntity.ok(inventarioService.obtenerStockBajo());
    }

    @Operation(summary = "Crear un registro de inventario",
            description = "Crea un registro de inventario verificando previamente, vía WebClient, que el producto asociado exista en el microservicio de productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Inventario creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Inventario.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"productoId\":10,\"stock\":100,\"stockMinimo\":10,\"ubicacion\":\"Bodega Central\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, o el producto asociado no existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Inventario> crear(@Valid @RequestBody InventarioDTO dto) {
        logger.info("POST /api/inventario");
        try {
            return ResponseEntity.status(201).body(inventarioService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear inventario: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar un registro de inventario", description = "Actualiza stock, stock mínimo o ubicación de un registro existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inventario actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No existe un inventario con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(
            @Parameter(description = "ID del inventario a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody InventarioDTO dto) {
        logger.info("PUT /api/inventario/{}", id);
        return inventarioService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un registro de inventario", description = "Elimina un registro de inventario existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Inventario eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un inventario con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del inventario a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/inventario/{}", id);
        return inventarioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
