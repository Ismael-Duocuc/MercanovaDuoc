package com.mercanoval.envios.service;

import com.mercanoval.envios.dto.EnvioDTO;
import com.mercanoval.envios.model.Envio;
import com.mercanoval.envios.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnvioService {

    private static final Logger logger = LoggerFactory.getLogger(EnvioService.class);
    private final EnvioRepository envioRepository;
    private final WebClient webClient;

    // Obtener todos los envíos
    public List<Envio> obtenerTodos() {
        logger.info("Obteniendo todos los envíos");
        return envioRepository.findAll();
    }

    // Obtener envío por ID
    public Optional<Envio> obtenerPorId(Long id) {
        logger.info("Buscando envío con ID: {}", id);
        return envioRepository.findById(id);
    }

    // Obtener envíos por pedido
    public List<Envio> obtenerPorPedido(Long pedidoId) {
        logger.info("Buscando envíos del pedido: {}", pedidoId);
        return envioRepository.findByPedidoId(pedidoId);
    }

    // Obtener envíos por estado
    public List<Envio> obtenerPorEstado(String estado) {
        logger.info("Buscando envíos con estado: {}", estado);
        return envioRepository.findByEstado(estado);
    }

    // Crear envío verificando pedido
    public Envio crear(EnvioDTO dto) {
        logger.info("Verificando pedido con ID: {}", dto.getPedidoId());

        // Verificar que el pedido existe
        Boolean pedidoExiste = webClient.get()
                .uri("http://localhost:8086/api/pedidos/" + dto.getPedidoId())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block();

        if (Boolean.FALSE.equals(pedidoExiste)) {
            logger.error("Pedido con ID {} no encontrado", dto.getPedidoId());
            throw new RuntimeException("Pedido no encontrado");
        }

        logger.info("Creando envío para pedido: {}", dto.getPedidoId());
        Envio envio = new Envio();
        envio.setPedidoId(dto.getPedidoId());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setEstado(dto.getEstado());
        envio.setTransportista(dto.getTransportista());
        return envioRepository.save(envio);
    }

    // Actualizar envío
    public Optional<Envio> actualizar(Long id, EnvioDTO dto) {
        logger.info("Actualizando envío con ID: {}", id);
        return envioRepository.findById(id).map(envio -> {
            envio.setPedidoId(dto.getPedidoId());
            envio.setDireccionDestino(dto.getDireccionDestino());
            envio.setEstado(dto.getEstado());
            envio.setTransportista(dto.getTransportista());
            return envioRepository.save(envio);
        });
    }

    // Eliminar envío
    public boolean eliminar(Long id) {
        logger.info("Eliminando envío con ID: {}", id);
        if (envioRepository.existsById(id)) {
            envioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}