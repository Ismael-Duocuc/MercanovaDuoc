package com.mercanoval.inventario.service;

import com.mercanoval.inventario.dto.InventarioDTO;
import com.mercanoval.inventario.model.Inventario;
import com.mercanoval.inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private static final Logger logger = LoggerFactory.getLogger(InventarioService.class);
    private final InventarioRepository inventarioRepository;
    private final WebClient webClient;

    // Obtener todos los inventarios
    public List<Inventario> obtenerTodos() {
        logger.info("Obteniendo todos los inventarios");
        return inventarioRepository.findAll();
    }

    // Obtener inventario por ID
    public Optional<Inventario> obtenerPorId(Long id) {
        logger.info("Buscando inventario con ID: {}", id);
        return inventarioRepository.findById(id);
    }

    // Obtener inventario por producto
    public Optional<Inventario> obtenerPorProducto(Long productoId) {
        logger.info("Buscando inventario del producto: {}", productoId);
        return inventarioRepository.findByProductoId(productoId);
    }

    // Obtener inventarios con stock bajo
    public List<Inventario> obtenerStockBajo() {
        logger.info("Buscando inventarios con stock bajo");
        return inventarioRepository.findByStockLessThanEqual(10);
    }

    // Crear inventario verificando producto
    public Inventario crear(InventarioDTO dto) {
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

        logger.info("Creando inventario para producto: {}", dto.getProductoId());
        Inventario inventario = new Inventario();
        inventario.setProductoId(dto.getProductoId());
        inventario.setStock(dto.getStock());
        inventario.setStockMinimo(dto.getStockMinimo());
        inventario.setUbicacion(dto.getUbicacion());
        return inventarioRepository.save(inventario);
    }

    // Actualizar inventario
    public Optional<Inventario> actualizar(Long id, InventarioDTO dto) {
        logger.info("Actualizando inventario con ID: {}", id);
        return inventarioRepository.findById(id).map(inventario -> {
            inventario.setProductoId(dto.getProductoId());
            inventario.setStock(dto.getStock());
            inventario.setStockMinimo(dto.getStockMinimo());
            inventario.setUbicacion(dto.getUbicacion());
            return inventarioRepository.save(inventario);
        });
    }

    // Eliminar inventario
    public boolean eliminar(Long id) {
        logger.info("Eliminando inventario con ID: {}", id);
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}