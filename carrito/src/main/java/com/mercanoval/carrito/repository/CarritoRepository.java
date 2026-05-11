package com.mercanoval.carrito.repository;

import com.mercanoval.carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    // Buscar items del carrito por cliente
    List<Carrito> findByClienteId(Long clienteId);

    // Buscar items del carrito por producto
    List<Carrito> findByProductoId(Long productoId);

    // Eliminar items del carrito por cliente
    void deleteByClienteId(Long clienteId);
}