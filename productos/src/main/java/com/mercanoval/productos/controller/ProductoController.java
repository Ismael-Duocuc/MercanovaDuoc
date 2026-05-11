package com.mercanoval.productos.controller;

import com.mercanoval.productos.dto.ProductoDTO;
import com.mercanoval.productos.model.Producto;
import com.mercanoval.productos.service.ProductoService;
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
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    // GET todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> obtenerTodos() {
        logger.info("GET /api/productos");
        return ResponseEntity.ok(productoService.obtenerTodos());
    }

    // GET producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/productos/{}", id);
        return productoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET productos por categoría
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> obtenerPorCategoria(@PathVariable String categoria) {
        logger.info("GET /api/productos/categoria/{}", categoria);
        return ResponseEntity.ok(productoService.obtenerPorCategoria(categoria));
    }

    // GET productos por proveedor
    @GetMapping("/proveedor/{proveedor}")
    public ResponseEntity<List<Producto>> obtenerPorProveedor(@PathVariable String proveedor) {
        logger.info("GET /api/productos/proveedor/{}", proveedor);
        return ResponseEntity.ok(productoService.obtenerPorProveedor(proveedor));
    }

    // POST crear producto
    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody ProductoDTO dto) {
        logger.info("POST /api/productos");
        return ResponseEntity.status(201).body(productoService.crear(dto));
    }

    // PUT actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        logger.info("PUT /api/productos/{}", id);
        return productoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/productos/{}", id);
        return productoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}