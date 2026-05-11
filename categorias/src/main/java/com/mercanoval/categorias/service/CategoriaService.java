package com.mercanoval.categorias.service;

import com.mercanoval.categorias.dto.CategoriaDTO;
import com.mercanoval.categorias.model.Categoria;
import com.mercanoval.categorias.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);
    private final CategoriaRepository categoriaRepository;

    // Obtener todas las categorías
    public List<Categoria> obtenerTodos() {
        logger.info("Obteniendo todas las categorías");
        return categoriaRepository.findAll();
    }

    // Obtener categoría por ID
    public Optional<Categoria> obtenerPorId(Long id) {
        logger.info("Buscando categoría con ID: {}", id);
        return categoriaRepository.findById(id);
    }

    // Crear categoría
    public Categoria crear(CategoriaDTO dto) {
        logger.info("Creando categoría: {}", dto.getNombre());
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        return categoriaRepository.save(categoria);
    }

    // Actualizar categoría
    public Optional<Categoria> actualizar(Long id, CategoriaDTO dto) {
        logger.info("Actualizando categoría con ID: {}", id);
        return categoriaRepository.findById(id).map(categoria -> {
            categoria.setNombre(dto.getNombre());
            categoria.setDescripcion(dto.getDescripcion());
            return categoriaRepository.save(categoria);
        });
    }

    // Eliminar categoría
    public boolean eliminar(Long id) {
        logger.info("Eliminando categoría con ID: {}", id);
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}