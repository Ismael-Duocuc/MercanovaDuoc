package com.mercanoval.descuentos.controller;

import com.mercanoval.descuentos.dto.DescuentoDTO;
import com.mercanoval.descuentos.model.Descuento;
import com.mercanoval.descuentos.service.DescuentoService;
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
public class DescuentoController {

    private static final Logger logger = LoggerFactory.getLogger(DescuentoController.class);
    private final DescuentoService descuentoService;

    // GET todos los descuentos
    @GetMapping
    public ResponseEntity<List<Descuento>> obtenerTodos() {
        logger.info("GET /api/descuentos");
        return ResponseEntity.ok(descuentoService.obtenerTodos());
    }

    // GET descuento por ID
    @GetMapping("/{id}")
    public ResponseEntity<Descuento> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/descuentos/{}", id);
        return descuentoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET descuento por código
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Descuento> obtenerPorCodigo(@PathVariable String codigo) {
        logger.info("GET /api/descuentos/codigo/{}", codigo);
        return descuentoService.obtenerPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET descuentos activos
    @GetMapping("/activos")
    public ResponseEntity<List<Descuento>> obtenerActivos() {
        logger.info("GET /api/descuentos/activos");
        return ResponseEntity.ok(descuentoService.obtenerActivos());
    }

    // POST crear descuento
    @PostMapping
    public ResponseEntity<Descuento> crear(@Valid @RequestBody DescuentoDTO dto) {
        logger.info("POST /api/descuentos");
        return ResponseEntity.status(201).body(descuentoService.crear(dto));
    }

    // PUT actualizar descuento
    @PutMapping("/{id}")
    public ResponseEntity<Descuento> actualizar(@PathVariable Long id, @Valid @RequestBody DescuentoDTO dto) {
        logger.info("PUT /api/descuentos/{}", id);
        return descuentoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar descuento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/descuentos/{}", id);
        return descuentoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}