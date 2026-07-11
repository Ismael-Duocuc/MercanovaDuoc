package com.mercanoval.categorias.controller;

import com.mercanoval.categorias.dto.CategoriaDTO;
import com.mercanoval.categorias.model.Categoria;
import com.mercanoval.categorias.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaService categoriaService;

    @Operation(summary = "Listar todas las categorías", description = "Retorna la lista completa de categorías registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Categoria.class)))
    })
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodos() {
        logger.info("GET /api/categorias");
        return ResponseEntity.ok(categoriaService.obtenerTodos());
    }

    @Operation(summary = "Obtener categoría por ID", description = "Busca y retorna una categoría según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(
            @Parameter(description = "ID de la categoría", example = "1") @PathVariable Long id) {
        logger.info("GET /api/categorias/{}", id);
        return categoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva categoría", description = "Registra una nueva categoría de productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada exitosamente",
                    content = @Content(schema = @Schema(implementation = Categoria.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"nombre\":\"Electrónica\",\"descripcion\":\"Productos electrónicos\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos en el body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody CategoriaDTO dto) {
        logger.info("POST /api/categorias");
        return ResponseEntity.status(201).body(categoriaService.crear(dto));
    }

    @Operation(summary = "Actualizar una categoría existente", description = "Actualiza los datos de una categoría ya registrada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente",
                    content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(
            @Parameter(description = "ID de la categoría a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody CategoriaDTO dto) {
        logger.info("PUT /api/categorias/{}", id);
        return categoriaService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una categoría", description = "Elimina una categoría existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la categoría a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/categorias/{}", id);
        return categoriaService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
