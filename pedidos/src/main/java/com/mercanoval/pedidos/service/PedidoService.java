package com.mercanoval.pedidos.service;

import com.mercanoval.pedidos.dto.PedidoDTO;
import com.mercanoval.pedidos.model.Pedido;
import com.mercanoval.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);
    private final PedidoRepository pedidoRepository;
    private final WebClient webClient;

    // Obtener todos los pedidos
    public List<Pedido> obtenerTodos() {
        logger.info("Obteniendo todos los pedidos");
        return pedidoRepository.findAll();
    }

    // Obtener pedido por ID
    public Optional<Pedido> obtenerPorId(Long id) {
        logger.info("Buscando pedido con ID: {}", id);
        return pedidoRepository.findById(id);
    }

    // Obtener pedidos por cliente
    public List<Pedido> obtenerPorCliente(Long clienteId) {
        logger.info("Buscando pedidos del cliente: {}", clienteId);
        return pedidoRepository.findByClienteId(clienteId);
    }

    // Obtener pedidos por estado
    public List<Pedido> obtenerPorEstado(String estado) {
        logger.info("Buscando pedidos con estado: {}", estado);
        return pedidoRepository.findByEstado(estado);
    }

    // Crear pedido verificando cliente y producto
    public Pedido crear(PedidoDTO dto) {
        logger.info("Verificando cliente con ID: {}", dto.getClienteId());

        // Verificar que el cliente existe
        Boolean clienteExiste = webClient.get()
                .uri("http://localhost:8081/api/clientes/" + dto.getClienteId())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block();

        if (Boolean.FALSE.equals(clienteExiste)) {
            logger.error("Cliente con ID {} no encontrado", dto.getClienteId());
            throw new RuntimeException("Cliente no encontrado");
        }

        logger.info("Verificando producto con ID: {}", dto.getProductoId());

        // Verificar que el producto existe
        Boolean productoExiste = webClient.get()
                .uri("http://localhost:8082/api/productos/" + dto.getProductoId())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block();

        if (Boolean.FALSE.equals(productoExiste)) {
            logger.error("Producto con ID {} no encontrado", dto.getProductoId());
            throw new RuntimeException("Producto no encontrado");
        }

        logger.info("Creando pedido para cliente: {}", dto.getClienteId());
        Pedido pedido = new Pedido();
        pedido.setClienteId(dto.getClienteId());
        pedido.setProductoId(dto.getProductoId());
        pedido.setCantidad(dto.getCantidad());
        pedido.setTotal(dto.getTotal());
        pedido.setEstado(dto.getEstado());
        return pedidoRepository.save(pedido);
    }

    // Actualizar pedido
    public Optional<Pedido> actualizar(Long id, PedidoDTO dto) {
        logger.info("Actualizando pedido con ID: {}", id);
        return pedidoRepository.findById(id).map(pedido -> {
            pedido.setClienteId(dto.getClienteId());
            pedido.setProductoId(dto.getProductoId());
            pedido.setCantidad(dto.getCantidad());
            pedido.setTotal(dto.getTotal());
            pedido.setEstado(dto.getEstado());
            return pedidoRepository.save(pedido);
        });
    }

    // Eliminar pedido
    public boolean eliminar(Long id) {
        logger.info("Eliminando pedido con ID: {}", id);
        if (pedidoRepository.existsById(id)) {
            pedidoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}