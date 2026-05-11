package com.mercanoval.inventario.repository;

import com.mercanoval.inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // Buscar inventario por producto
    Optional<Inventario> findByProductoId(Long productoId);

    // Buscar inventarios con stock menor al mínimo
    List<Inventario> findByStockLessThanEqual(Integer stock);

    // Buscar inventario por ubicación
    List<Inventario> findByUbicacion(String ubicacion);
}