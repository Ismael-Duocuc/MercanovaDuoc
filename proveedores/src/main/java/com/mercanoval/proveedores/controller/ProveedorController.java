package com.mercanoval.proveedores.controller;

import com.mercanoval.proveedores.dto.ProveedorDTO;
import com.mercanoval.proveedores.model.Proveedor;
import com.mercanoval.proveedores.service.ProveedorService;
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
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;

    // GET todos los proveedores
    @GetMapping
    public ResponseEntity<List<Proveedor>> obtenerTodos() {
        logger.info("GET /api/proveedores");
        return ResponseEntity.ok(proveedorService.obtenerTodos());
    }

    // GET proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/proveedores/{}", id);
        return proveedorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET proveedores por país
    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<Proveedor>> obtenerPorPais(@PathVariable String pais) {
        logger.info("GET /api/proveedores/pais/{}", pais);
        return ResponseEntity.ok(proveedorService.obtenerPorPais(pais));
    }

    // POST crear proveedor
    @PostMapping
    public ResponseEntity<Proveedor> crear(@Valid @RequestBody ProveedorDTO dto) {
        logger.info("POST /api/proveedores");
        return ResponseEntity.status(201).body(proveedorService.crear(dto));
    }

    // PUT actualizar proveedor
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorDTO dto) {
        logger.info("PUT /api/proveedores/{}", id);
        return proveedorService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar proveedor
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/proveedores/{}", id);
        return proveedorService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}