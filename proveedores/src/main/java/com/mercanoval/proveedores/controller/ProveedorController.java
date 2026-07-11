package com.mercanoval.proveedores.controller;

import com.mercanoval.proveedores.dto.ProveedorDTO;
import com.mercanoval.proveedores.model.Proveedor;
import com.mercanoval.proveedores.service.ProveedorService;
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
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de proveedores")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;

    @Operation(summary = "Listar todos los proveedores", description = "Retorna la lista completa de proveedores registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Proveedor.class)))
    })
    @GetMapping
    public ResponseEntity<List<Proveedor>> obtenerTodos() {
        logger.info("GET /api/proveedores");
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }

    @Operation(summary = "Obtener proveedor por ID", description = "Busca y retorna un proveedor según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proveedor encontrado",
                    content = @Content(schema = @Schema(implementation = Proveedor.class))),
            @ApiResponse(responseCode = "404", description = "No existe un proveedor con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(
            @Parameter(description = "ID del proveedor", example = "1") @PathVariable Long id) {
        logger.info("GET /api/proveedores/{}", id);
        return proveedorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar proveedores por país", description = "Retorna todos los proveedores ubicados en el país indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de proveedores del país",
                    content = @Content(schema = @Schema(implementation = Proveedor.class)))
    })
    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Proveedor>> obtenerPorPais(
            @Parameter(description = "Nombre del país", example = "Chile") @PathVariable String pais) {
        logger.info("GET /api/proveedores/pais/{}", pais);
        return ResponseEntity.ok(proveedorService.obtenerPorPais(pais));
    }

    @Operation(summary = "Crear un nuevo proveedor", description = "Registra un nuevo proveedor en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Proveedor.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Tech Import SPA\",\"email\":\"ventas@techimport.cl\",\"telefono\":\"+56987654321\",\"direccion\":\"Los Militares 5500\",\"pais\":\"Chile\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos en el body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Proveedor> crear(@Valid @RequestBody ProveedorDTO dto) {
        logger.info("POST /api/proveedores");
        return ResponseEntity.status(201).body(proveedorService.crear(dto));
    }

    @Operation(summary = "Actualizar un proveedor existente", description = "Actualiza los datos de un proveedor ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Proveedor actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Proveedor.class))),
            @ApiResponse(responseCode = "404", description = "No existe un proveedor con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(
            @Parameter(description = "ID del proveedor a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProveedorDTO dto) {
        logger.info("PUT /api/proveedores/{}", id);
        return proveedorService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un proveedor", description = "Elimina un proveedor existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Proveedor eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un proveedor con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del proveedor a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/proveedores/{}", id);
        return proveedorService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
