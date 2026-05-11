package com.mercanoval.pedidos.repository;

import com.mercanoval.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por cliente
    List<Pedido> findByClienteId(Long clienteId);

    // Buscar pedidos por estado
    List<Pedido> findByEstado(String estado);

    // Buscar pedidos por producto
    List<Pedido> findByProductoId(Long productoId);
}