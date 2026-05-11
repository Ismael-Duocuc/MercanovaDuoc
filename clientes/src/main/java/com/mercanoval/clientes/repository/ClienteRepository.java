package com.mercanoval.clientes.repository;

import com.mercanoval.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Buscar cliente por email
    Optional<Cliente> findByEmail(String email);
}