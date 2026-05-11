package com.mercanoval.productos.service;

import com.mercanoval.productos.dto.ProductoDTO;
import com.mercanoval.productos.model.Producto;
import com.mercanoval.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);
    private final ProductoRepository productoRepository;

    // Obtener todos los productos
    public List<Producto> obtenerTodos() {
        logger.info("Obteniendo todos los productos");
        return productoRepository.findAll();
    }

    // Obtener producto por ID
    public Optional<Producto> obtenerPorId(Long id) {
        logger.info("Buscando producto con ID: {}", id);
        return productoRepository.findById(id);
    }

    // Obtener productos por categoría
    public List<Producto> obtenerPorCategoria(String categoria) {
        logger.info("Buscando productos por categoría: {}", categoria);
        return productoRepository.findByCategoria(categoria);
    }

    // Obtener productos por proveedor
    public List<Producto> obtenerPorProveedor(String proveedor) {
        logger.info("Buscando productos por proveedor: {}", proveedor);
        return productoRepository.findByProveedor(proveedor);
    }

    // Crear producto
    public Producto crear(ProductoDTO dto) {
        logger.info("Creando producto: {}", dto.getNombre());
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setCategoria(dto.getCategoria());
        producto.setProveedor(dto.getProveedor());
        return productoRepository.save(producto);
    }

    // Actualizar producto
    public Optional<Producto> actualizar(Long id, ProductoDTO dto) {
        logger.info("Actualizando producto con ID: {}", id);
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(dto.getNombre());
            producto.setDescripcion(dto.getDescripcion());
            producto.setPrecio(dto.getPrecio());
            producto.setCategoria(dto.getCategoria());
            producto.setProveedor(dto.getProveedor());
            return productoRepository.save(producto);
        });
    }

    // Eliminar producto
    public boolean eliminar(Long id) {
        logger.info("Eliminando producto con ID: {}", id);
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}