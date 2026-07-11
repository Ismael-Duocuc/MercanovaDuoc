package com.mercanoval.descuentos.controller;

import com.mercanoval.descuentos.dto.DescuentoDTO;
import com.mercanoval.descuentos.model.Descuento;
import com.mercanoval.descuentos.service.DescuentoService;
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
@RequestMapping("/api/descuentos")
@RequiredArgsConstructor
@Tag(name = "Descuentos", description = "Gestión de códigos de descuento")
public class DescuentoController {

    private static final Logger logger = LoggerFactory.getLogger(DescuentoController.class);
    private final DescuentoService descuentoService;

    @Operation(summary = "Listar todos los descuentos", description = "Retorna la lista completa de descuentos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de descuentos obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Descuento.class)))
    })
    @GetMapping
    public ResponseEntity<List<Descuento>> obtenerTodos() {
        logger.info("GET /api/descuentos");
        return ResponseEntity.ok(descuentoService.obtenerTodos());
    }

    @Operation(summary = "Obtener descuento por ID", description = "Busca y retorna un descuento según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Descuento encontrado",
                    content = @Content(schema = @Schema(implementation = Descuento.class))),
            @ApiResponse(responseCode = "404", description = "No existe un descuento con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Descuento> obtenerPorId(
            @Parameter(description = "ID del descuento", example = "1") @PathVariable Long id) {
        logger.info("GET /api/descuentos/{}", id);
        return descuentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener descuento por código",
            description = "Busca un descuento según su código único. Este endpoint es consumido internamente por el microservicio de pago para aplicar descuentos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Descuento encontrado",
                    content = @Content(schema = @Schema(implementation = Descuento.class))),
            @ApiResponse(responseCode = "404", description = "No existe un descuento con ese código", content = @Content)
    })
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Descuento> obtenerPorCodigo(
            @Parameter(description = "Código del descuento", example = "VERANO25") @PathVariable String codigo) {
        logger.info("GET /api/descuentos/codigo/{}", codigo);
        return descuentoService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar descuentos activos", description = "Retorna solo los descuentos actualmente activos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de descuentos activos",
                    content = @Content(schema = @Schema(implementation = Descuento.class)))
    })
    @GetMapping("/activos")
    public ResponseEntity<List<Descuento>> obtenerActivos() {
        logger.info("GET /api/descuentos/activos");
        return ResponseEntity.ok(descuentoService.obtenerActivos());
    }

    @Operation(summary = "Crear un nuevo descuento", description = "Registra un nuevo código de descuento en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Descuento creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Descuento.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"codigo\":\"VERANO25\",\"descripcion\":\"Descuento de verano\",\"porcentaje\":25.0,\"activo\":true}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos en el body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Descuento> crear(@Valid @RequestBody DescuentoDTO dto) {
        logger.info("POST /api/descuentos");
        return ResponseEntity.status(201).body(descuentoService.crear(dto));
    }

    @Operation(summary = "Actualizar un descuento existente", description = "Actualiza los datos de un descuento ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Descuento actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Descuento.class))),
            @ApiResponse(responseCode = "404", description = "No existe un descuento con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Descuento> actualizar(
            @Parameter(description = "ID del descuento a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody DescuentoDTO dto) {
        logger.info("PUT /api/descuentos/{}", id);
        return descuentoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un descuento", description = "Elimina un descuento existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Descuento eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un descuento con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del descuento a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/descuentos/{}", id);
        return descuentoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
