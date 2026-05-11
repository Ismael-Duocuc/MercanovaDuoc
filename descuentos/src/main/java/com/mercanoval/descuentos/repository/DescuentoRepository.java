package com.mercanoval.descuentos.repository;

import com.mercanoval.descuentos.model.Descuento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {

    // Buscar descuento por código
    Optional<Descuento> findByCodigo(String codigo);

    // Buscar descuentos activos
    List<Descuento> findByActivo(Boolean activo);
}