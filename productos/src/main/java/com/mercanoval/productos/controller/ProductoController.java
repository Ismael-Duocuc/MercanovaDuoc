package com.mercanoval.productos.controller;

import com.mercanoval.productos.dto.ProductoDTO;
import com.mercanoval.productos.model.Producto;
import com.mercanoval.productos.service.ProductoService;
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
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    @Operation(summary = "Listar todos los productos", description = "Retorna el catálogo completo de productos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Producto.class)))
    })
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        logger.info("GET /api/productos");
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca y retorna un producto específico según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(
            @Parameter(description = "ID del producto", example = "1") @PathVariable Long id) {
        logger.info("GET /api/productos/{}", id);
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar productos por categoría", description = "Retorna todos los productos que pertenecen a la categoría indicada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos de la categoría",
                    content = @Content(schema = @Schema(implementation = Producto.class)))
    })
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> obtenerPorCategoria(
            @Parameter(description = "Nombre de la categoría", example = "Electrónica") @PathVariable String categoria) {
        logger.info("GET /api/productos/categoria/{}", categoria);
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    @Operation(summary = "Buscar productos por proveedor", description = "Retorna todos los productos asociados al proveedor indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos del proveedor",
                    content = @Content(schema = @Schema(implementation = Producto.class)))
    })
    @GetMapping("/proveedor/{proveedor}")
    public ResponseEntity<List<Producto>> obtenerPorProveedor(
            @Parameter(description = "Nombre del proveedor", example = "Tech Import SPA") @PathVariable String proveedor) {
        logger.info("GET /api/productos/proveedor/{}", proveedor);
        return ResponseEntity.ok(productoService.obtenerPorProveedor(proveedor));
    }

    @Operation(summary = "Crear un nuevo producto", description = "Registra un nuevo producto en el catálogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(
                                    value = "{\"id\":1,\"nombre\":\"Notebook Lenovo ThinkPad\",\"descripcion\":\"16GB RAM\",\"precio\":599990.0,\"categoria\":\"Electrónica\",\"proveedor\":\"Tech Import SPA\"}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos en el body", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO dto) {
        logger.info("POST /api/productos");
        return ResponseEntity.status(201).body(productoService.crear(dto));
    }

    @Operation(summary = "Actualizar un producto existente", description = "Actualiza los datos de un producto ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @Parameter(description = "ID del producto a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductoDTO dto) {
        logger.info("PUT /api/productos/{}", id);
        return productoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un producto", description = "Elimina un producto existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del producto a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/productos/{}", id);
        return productoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
