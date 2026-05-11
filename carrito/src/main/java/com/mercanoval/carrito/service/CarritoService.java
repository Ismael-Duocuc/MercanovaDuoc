package com.mercanoval.carrito.service;

import com.mercanoval.carrito.dto.CarritoDTO;
import com.mercanoval.carrito.model.Carrito;
import com.mercanoval.carrito.repository.CarritoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private static final Logger logger = LoggerFactory.getLogger(CarritoService.class);
    private final CarritoRepository carritoRepository;
    private final WebClient webClient;

    // Obtener todos los items del carrito
    public List<Carrito> obtenerTodos() {
        logger.info("Obteniendo todos los items del carrito");
        return carritoRepository.findAll();
    }

    // Obtener item por ID
    public Optional<Carrito> obtenerPorId(Long id) {
        logger.info("Buscando item con ID: {}", id);
        return carritoRepository.findById(id);
    }

    // Obtener carrito por cliente
    public List<Carrito> obtenerPorCliente(Long clienteId) {
        logger.info("Buscando carrito del cliente: {}", clienteId);
        return carritoRepository.findByClienteId(clienteId);
    }

    // Agregar item al carrito
    public Carrito agregar(CarritoDTO dto) {
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

        logger.info("Agregando producto {} al carrito del cliente {}", dto.getProductoId(), dto.getClienteId());
        Carrito carrito = new Carrito();
        carrito.setClienteId(dto.getClienteId());
        carrito.setProductoId(dto.getProductoId());
        carrito.setCantidad(dto.getCantidad());
        carrito.setPrecioUnitario(dto.getPrecioUnitario());
        carrito.setTotal(dto.getTotal());
        return carritoRepository.save(carrito);
    }

    // Actualizar item del carrito
    public Optional<Carrito> actualizar(Long id, CarritoDTO dto) {
        logger.info("Actualizando item del carrito con ID: {}", id);
        return carritoRepository.findById(id).map(carrito -> {
            carrito.setClienteId(dto.getClienteId());
            carrito.setProductoId(dto.getProductoId());
            carrito.setCantidad(dto.getCantidad());
            carrito.setPrecioUnitario(dto.getPrecioUnitario());
            carrito.setTotal(dto.getTotal());
            return carritoRepository.save(carrito);
        });
    }

    // Eliminar item del carrito
    public boolean eliminar(Long id) {
        logger.info("Eliminando item del carrito con ID: {}", id);
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Vaciar carrito de un cliente
    public void vaciarCarrito(Long clienteId) {
        logger.info("Vaciando carrito del cliente: {}", clienteId);
        carritoRepository.deleteByClienteId(clienteId);
    }
}