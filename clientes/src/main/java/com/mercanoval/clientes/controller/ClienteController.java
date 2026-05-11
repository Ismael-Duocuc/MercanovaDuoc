package com.mercanoval.clientes.controller;

import com.mercanoval.clientes.dto.ClienteDTO;
import com.mercanoval.clientes.model.Cliente;
import com.mercanoval.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    // GET todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        logger.info("GET /api/clientes");
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }

    // GET cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/clientes/{}", id);
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST crear cliente
    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody ClienteDTO dto) {
        logger.info("POST /api/clientes");
        return ResponseEntity.status(201).body(clienteService.crear(dto));
    }

    // PUT actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        logger.info("PUT /api/clientes/{}", id);
        return clienteService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/clientes/{}", id);
        return clienteService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}