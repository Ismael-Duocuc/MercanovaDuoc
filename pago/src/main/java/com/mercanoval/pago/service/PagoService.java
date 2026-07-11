package com.mercanoval.pago.service;

import com.mercanoval.pago.dto.PagoDTO;
import com.mercanoval.pago.model.Pago;
import com.mercanoval.pago.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private static final Logger logger = LoggerFactory.getLogger(PagoService.class);
    private final PagoRepository pagoRepository;
    private final WebClient webClient;

    @Value("${servicios.pedidos.url}")
    private String pedidosUrl;

    @Value("${servicios.descuentos.url}")
    private String descuentosUrl;

    // Obtener todos los pagos
    public List<Pago> obtenerTodos() {
        logger.info("Obteniendo todos los pagos");
        return pagoRepository.findAll();
    }

    // Obtener pago por ID
    public Optional<Pago> obtenerPorId(Long id) {
        logger.info("Buscando pago con ID: {}", id);
        return pagoRepository.findById(id);
    }

    // Obtener pagos por pedido
    public List<Pago> obtenerPorPedido(Long pedidoId) {
        logger.info("Buscando pagos del pedido: {}", pedidoId);
        return pagoRepository.findByPedidoId(pedidoId);
    }

    // Obtener pagos por estado
    public List<Pago> obtenerPorEstado(String estado) {
        logger.info("Buscando pagos con estado: {}", estado);
        return pagoRepository.findByEstado(estado);
    }

    // Crear pago verificando pedido y descuento
    public Pago crear(PagoDTO dto) {
        logger.info("Verificando pedido con ID: {}", dto.getPedidoId());

        // Verificar que el pedido existe
        Boolean pedidoExiste = webClient.get()
                .uri(pedidosUrl + "/api/pedidos/" + dto.getPedidoId())
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorReturn(false)
                .block();

        if (Boolean.FALSE.equals(pedidoExiste)) {
            logger.error("Pedido con ID {} no encontrado", dto.getPedidoId());
            throw new RuntimeException("Pedido no encontrado");
        }

        Pago pago = new Pago();
        pago.setPedidoId(dto.getPedidoId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado(dto.getEstado());
        pago.setDescuentoAplicado(0.0);

        // Verificar si hay código de descuento
        if (dto.getCodigoDescuento() != null && !dto.getCodigoDescuento().isEmpty()) {
            logger.info("Verificando descuento con código: {}", dto.getCodigoDescuento());

            try {
                // Obtener descuento
                java.util.Map descuento = webClient.get()
                        .uri(descuentosUrl + "/api/descuentos/codigo/" + dto.getCodigoDescuento())
                        .retrieve()
                        .bodyToMono(java.util.Map.class)
                        .block();

                if (descuento != null && Boolean.TRUE.equals(descuento.get("activo"))) {
                    Double porcentaje = ((Number) descuento.get("porcentaje")).doubleValue();
                    Double descuentoAplicado = dto.getMonto() * porcentaje / 100;
                    pago.setCodigoDescuento(dto.getCodigoDescuento());
                    pago.setDescuentoAplicado(descuentoAplicado);
                    pago.setMonto(dto.getMonto() - descuentoAplicado);
                    logger.info("Descuento aplicado: {}%", porcentaje);
                }
            } catch (Exception e) {
                logger.error("Error al aplicar descuento: {}", e.getMessage());
            }
        }

        return pagoRepository.save(pago);
    }

    // Actualizar pago
    public Optional<Pago> actualizar(Long id, PagoDTO dto) {
        logger.info("Actualizando pago con ID: {}", id);
        return pagoRepository.findById(id).map(pago -> {
            pago.setPedidoId(dto.getPedidoId());
            pago.setMonto(dto.getMonto());
            pago.setMetodoPago(dto.getMetodoPago());
            pago.setEstado(dto.getEstado());
            pago.setCodigoDescuento(dto.getCodigoDescuento());
            return pagoRepository.save(pago);
        });
    }

    // Eliminar pago
    public boolean eliminar(Long id) {
        logger.info("Eliminando pago con ID: {}", id);
        if (pagoRepository.existsById(id)) {
            pagoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}