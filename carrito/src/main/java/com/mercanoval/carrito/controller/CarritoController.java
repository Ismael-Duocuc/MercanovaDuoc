package com.mercanoval.carrito.controller;

import com.mercanoval.carrito.dto.CarritoDTO;
import com.mercanoval.carrito.model.Carrito;
import com.mercanoval.carrito.service.CarritoService;
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
public class CarritoController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);
    private final CarritoService carritoService;

    // GET todos los items
    @GetMapping
    public ResponseEntity<List<Carrito>> obtenerTodos() {
        logger.info("GET /api/carrito");
        return ResponseEntity.ok(carritoService.obtenerTodos());
    }

    // GET item por ID
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/carrito/{}", id);
        return carritoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET carrito por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Carrito>> obtenerPorCliente(@PathVariable Long clienteId) {
        logger.info("GET /api/carrito/cliente/{}", clienteId);
        return ResponseEntity.ok(carritoService.obtenerPorCliente(clienteId));
    }

    // POST agregar item al carrito
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

    // PUT actualizar item del carrito
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizar(@PathVariable Long id, @Valid @RequestBody CarritoDTO dto) {
        logger.info("PUT /api/carrito/{}", id);
        return carritoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar item del carrito
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/carrito/{}", id);
        return carritoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // DELETE vaciar carrito de un cliente
    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long clienteId) {
        logger.info("DELETE /api/carrito/cliente/{}", clienteId);
        carritoService.vaciarCarrito(clienteId);
        return ResponseEntity.noContent().build();
    }
}