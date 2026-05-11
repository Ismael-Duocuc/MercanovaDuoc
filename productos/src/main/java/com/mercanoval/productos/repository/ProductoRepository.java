package com.mercanoval.productos.repository;

import com.mercanoval.productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar productos por categoría
    List<Producto> findByCategoria(String categoria);

    // Buscar productos por proveedor
    List<Producto> findByProveedor(String proveedor);
}