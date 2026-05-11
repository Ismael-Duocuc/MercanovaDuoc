package com.mercanoval.inventario.controller;

import com.mercanoval.inventario.dto.InventarioDTO;
import com.mercanoval.inventario.model.Inventario;
import com.mercanoval.inventario.service.InventarioService;
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
public class InventarioController {

    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);
    private final InventarioService inventarioService;

    // GET todos los inventarios
    @GetMapping
    public ResponseEntity<List<Inventario>> obtenerTodos() {
        logger.info("GET /api/inventario");
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }

    // GET inventario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/inventario/{}", id);
        return inventarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET inventario por producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> obtenerPorProducto(@PathVariable Long productoId) {
        logger.info("GET /api/inventario/producto/{}", productoId);
        return inventarioService.obtenerPorProducto(productoId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET inventarios con stock bajo
    @GetMapping("/stockbajo")
    public ResponseEntity<List<Inventario>> obtenerStockBajo() {
        logger.info("GET /api/inventario/stockbajo");
        return ResponseEntity.ok(inventarioService.obtenerStockBajo());
    }

    // POST crear inventario
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

    // PUT actualizar inventario
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizar(@PathVariable Long id, @Valid @RequestBody InventarioDTO dto) {
        logger.info("PUT /api/inventario/{}", id);
        return inventarioService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar inventario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/inventario/{}", id);
        return inventarioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}