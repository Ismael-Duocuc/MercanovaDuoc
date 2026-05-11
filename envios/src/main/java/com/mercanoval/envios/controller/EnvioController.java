package com.mercanoval.envios.controller;

import com.mercanoval.envios.dto.EnvioDTO;
import com.mercanoval.envios.model.Envio;
import com.mercanoval.envios.service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private static final Logger logger = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;

    // GET todos los envíos
    @GetMapping
    public ResponseEntity<List<Envio>> obtenerTodos() {
        logger.info("GET /api/envios");
        return ResponseEntity.ok(envioService.obtenerTodos());
    }

    // GET envío por ID
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/envios/{}", id);
        return envioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET envíos por pedido
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Envio>> obtenerPorPedido(@PathVariable Long pedidoId) {
        logger.info("GET /api/envios/pedido/{}", pedidoId);
        return ResponseEntity.ok(envioService.obtenerPorPedido(pedidoId));
    }

    // GET envíos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Envio>> obtenerPorEstado(@PathVariable String estado) {
        logger.info("GET /api/envios/estado/{}", estado);
        return ResponseEntity.ok(envioService.obtenerPorEstado(estado));
    }

    // POST crear envío
    @PostMapping
    public ResponseEntity<Envio> crear(@Valid @RequestBody EnvioDTO dto) {
        logger.info("POST /api/envios");
        try {
            return ResponseEntity.status(201).body(envioService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear envío: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT actualizar envío
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizar(@PathVariable Long id, @Valid @RequestBody EnvioDTO dto) {
        logger.info("PUT /api/envios/{}", id);
        return envioService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar envío
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/envios/{}", id);
        return envioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}