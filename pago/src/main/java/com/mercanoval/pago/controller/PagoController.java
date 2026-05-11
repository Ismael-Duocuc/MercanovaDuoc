package com.mercanoval.pago.controller;

import com.mercanoval.pago.dto.PagoDTO;
import com.mercanoval.pago.model.Pago;
import com.mercanoval.pago.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;

    // GET todos los pagos
    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodos() {
        logger.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.obtenerTodos());
    }

    // GET pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/pagos/{}", id);
        return pagoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET pagos por pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Pago>> obtenerPorPedido(@PathVariable Long pedidoId) {
        logger.info("GET /api/pagos/pedido/{}", pedidoId);
        return ResponseEntity.ok(pagoService.obtenerPorPedido(pedidoId));
    }

    // GET pagos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> obtenerPorEstado(@PathVariable String estado) {
        logger.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.obtenerPorEstado(estado));
    }

    // POST crear pago
    @PostMapping
    public ResponseEntity<Pago> crear(@Valid @RequestBody PagoDTO dto) {
        logger.info("POST /api/pagos");
        try {
            return ResponseEntity.status(201).body(pagoService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear pago: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT actualizar pago
    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(@PathVariable Long id, @Valid @RequestBody PagoDTO dto) {
        logger.info("PUT /api/pagos/{}", id);
        return pagoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar pago
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/pagos/{}", id);
        return pagoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}