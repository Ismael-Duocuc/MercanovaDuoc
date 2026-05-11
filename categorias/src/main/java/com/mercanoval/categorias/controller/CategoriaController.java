package com.mercanoval.categorias.controller;

import com.mercanoval.categorias.dto.CategoriaDTO;
import com.mercanoval.categorias.model.Categoria;
import com.mercanoval.categorias.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaController.class);
    private final CategoriaService categoriaService;

    // GET todas las categorías
    @GetMapping
    public ResponseEntity<List<Categoria>> obtenerTodos() {
        logger.info("GET /api/categorias");
        return ResponseEntity.ok(categoriaService.obtenerTodos());
    }

    // GET categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /api/categorias/{}", id);
        return categoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST crear categoría
    @PostMapping
    public ResponseEntity<Categoria> crear(@Valid @RequestBody CategoriaDTO dto) {
        logger.info("POST /api/categorias");
        return ResponseEntity.status(201).body(categoriaService.crear(dto));
    }

    // PUT actualizar categoría
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        logger.info("PUT /api/categorias/{}", id);
        return categoriaService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE eliminar categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/categorias/{}", id);
        return categoriaService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}