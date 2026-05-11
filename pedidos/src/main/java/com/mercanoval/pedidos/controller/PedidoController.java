package com.mercanoval.pedidos.controller;

import com.mercanoval.pedidos.dto.PedidoDTO;
import com.mercanoval.pedidos.model.Pedido;
import com.mercanoval.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoService pedidoService;

    // GET todos los pedidos
    @GetMapping
    public ResponseEntity<List<Pedido>> obtenerTodos() {
        logger.info("GET /api/pedidos");
        return ResponseEntity.ok(pedidoService.obtenerTodos());
    }

    // GET pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/pedidos/{}", id);
        return pedidoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET pedidos por cliente
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Pedido>> obtenerPorCliente(@PathVariable Long clienteId) {
        logger.info("GET /api/pedidos/cliente/{}", clienteId);
        return ResponseEntity.ok(pedidoService.obtenerPorCliente(clienteId));
    }

    // GET pedidos por estado
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pedido>> obtenerPorEstado(@PathVariable String estado) {
        logger.info("GET /api/pedidos/estado/{}", estado);
        return ResponseEntity.ok(pedidoService.obtenerPorEstado(estado));
    }

    // POST crear pedido
    @PostMapping
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoDTO dto) {
        logger.info("POST /api/pedidos");
        try {
            return ResponseEntity.status(201).body(pedidoService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear pedido: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT actualizar pedido
    @PutMapping("/{id}")
    public ResponseEntity<Pedido> actualizar(@PathVariable Long id, @Valid @RequestBody PedidoDTO dto) {
        logger.info("PUT /api/pedidos/{}", id);
        return pedidoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar pedido
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/pedidos/{}", id);
        return pedidoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}