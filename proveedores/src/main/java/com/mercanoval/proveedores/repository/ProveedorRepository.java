package com.mercanoval.proveedores.repository;

import com.mercanoval.proveedores.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    // Buscar proveedor por email
    Optional<Proveedor> findByEmail(String email);

    // Buscar proveedor por país
    java.util.List<Proveedor> findByPais(String pais);
}