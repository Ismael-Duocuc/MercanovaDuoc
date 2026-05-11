package com.mercanoval.proveedores.service;

import com.mercanoval.proveedores.dto.ProveedorDTO;
import com.mercanoval.proveedores.model.Proveedor;
import com.mercanoval.proveedores.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);
    private final ProveedorRepository proveedorRepository;

    // Obtener todos los proveedores
    public List<Proveedor> obtenerTodos() {
        logger.info("Obteniendo todos los proveedores");
        return proveedorRepository.findAll();
    }

    // Obtener proveedor por ID
    public Optional<Proveedor> obtenerPorId(Long id) {
        logger.info("Buscando proveedor con ID: {}", id);
        return proveedorRepository.findById(id);
    }

    // Obtener proveedores por país
    public List<Proveedor> obtenerPorPais(String pais) {
        logger.info("Buscando proveedores por país: {}", pais);
        return proveedorRepository.findByPais(pais);
    }

    // Crear proveedor
    public Proveedor crear(ProveedorDTO dto) {
        logger.info("Creando proveedor: {}", dto.getNombre());
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(dto.getNombre());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setPais(dto.getPais());
        return proveedorRepository.save(proveedor);
    }

    // Actualizar proveedor
    public Optional<Proveedor> actualizar(Long id, ProveedorDTO dto) {
        logger.info("Actualizando proveedor con ID: {}", id);
        return proveedorRepository.findById(id).map(proveedor -> {
            proveedor.setNombre(dto.getNombre());
            proveedor.setEmail(dto.getEmail());
            proveedor.setTelefono(dto.getTelefono());
            proveedor.setDireccion(dto.getDireccion());
            proveedor.setPais(dto.getPais());
            return proveedorRepository.save(proveedor);
        });
    }

    // Eliminar proveedor
    public boolean eliminar(Long id) {
        logger.info("Eliminando proveedor con ID: {}", id);
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}