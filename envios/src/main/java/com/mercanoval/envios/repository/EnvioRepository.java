package com.mercanoval.envios.repository;

import com.mercanoval.envios.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    // Buscar envíos por pedido
    List<Envio> findByPedidoId(Long pedidoId);

    // Buscar envíos por estado
    List<Envio> findByEstado(String estado);

    // Buscar envíos por transportista
    List<Envio> findByTransportista(String transportista);
}