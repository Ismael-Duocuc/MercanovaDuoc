package com.mercanoval.clientes.service;

import com.mercanoval.clientes.dto.ClienteDTO;
import com.mercanoval.clientes.model.Cliente;
import com.mercanoval.clientes.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);
    private final ClienteRepository clienteRepository;

    // Obtener todos los clientes
    public List<Cliente> obtenerTodos() {
        logger.info("Obteniendo todos los clientes");
        return clienteRepository.findAll();
    }

    // Obtener cliente por ID
    public Optional<Cliente> obtenerPorId(Long id) {
        logger.info("Buscando cliente con ID: {}", id);
        return clienteRepository.findById(id);
    }

    // Crear cliente
    public Cliente crear(ClienteDTO dto) {
        logger.info("Creando cliente con email: {}", dto.getEmail());
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setDireccion(dto.getDireccion());
        return clienteRepository.save(cliente);
    }

    // Actualizar cliente
    public Optional<Cliente> actualizar(Long id, ClienteDTO dto) {
        logger.info("Actualizando cliente con ID: {}", id);
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(dto.getNombre());
            cliente.setEmail(dto.getEmail());
            cliente.setTelefono(dto.getTelefono());
            cliente.setDireccion(dto.getDireccion());
            return clienteRepository.save(cliente);
        });
    }

    // Eliminar cliente
    public boolean eliminar(Long id) {
        logger.info("Eliminando cliente con ID: {}", id);
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}