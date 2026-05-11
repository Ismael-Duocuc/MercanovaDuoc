package com.mercanoval.pago.repository;

import com.mercanoval.pago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar pagos por pedido
    List<Pago> findByPedidoId(Long pedidoId);

    // Buscar pagos por estado
    List<Pago> findByEstado(String estado);

    // Buscar pagos por método de pago
    List<Pago> findByMetodoPago(String metodoPago);
}