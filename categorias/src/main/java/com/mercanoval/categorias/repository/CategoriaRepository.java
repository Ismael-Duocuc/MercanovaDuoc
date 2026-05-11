package com.mercanoval.categorias.repository;

import com.mercanoval.categorias.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Buscar categoría por nombre
    Optional<Categoria> findByNombre(String nombre);
}