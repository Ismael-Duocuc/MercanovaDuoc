package com.mercanoval.descuentos.service;

import com.mercanoval.descuentos.dto.DescuentoDTO;
import com.mercanoval.descuentos.model.Descuento;
import com.mercanoval.descuentos.repository.DescuentoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DescuentoService {

    private static final Logger logger = LoggerFactory.getLogger(DescuentoService.class);
    private final DescuentoRepository descuentoRepository;

    // Obtener todos los descuentos
    public List<Descuento> obtenerTodos() {
        logger.info("Obteniendo todos los descuentos");
        return descuentoRepository.findAll();
    }

    // Obtener descuento por ID
    public Optional<Descuento> obtenerPorId(Long id) {
        logger.info("Buscando descuento con ID: {}", id);
        return descuentoRepository.findById(id);
    }

    // Obtener descuento por código
    public Optional<Descuento> obtenerPorCodigo(String codigo) {
        logger.info("Buscando descuento con código: {}", codigo);
        return descuentoRepository.findByCodigo(codigo);
    }

    // Obtener descuentos activos
    public List<Descuento> obtenerActivos() {
        logger.info("Obteniendo descuentos activos");
        return descuentoRepository.findByActivo(true);
    }

    // Crear descuento
    public Descuento crear(DescuentoDTO dto) {
        logger.info("Creando descuento con código: {}", dto.getCodigo());
        Descuento descuento = new Descuento();
        descuento.setCodigo(dto.getCodigo());
        descuento.setDescripcion(dto.getDescripcion());
        descuento.setPorcentaje(dto.getPorcentaje());
        descuento.setActivo(dto.getActivo());
        return descuentoRepository.save(descuento);
    }

    // Actualizar descuento
    public Optional<Descuento> actualizar(Long id, DescuentoDTO dto) {
        logger.info("Actualizando descuento con ID: {}", id);
        return descuentoRepository.findById(id).map(descuento -> {
            descuento.setCodigo(dto.getCodigo());
            descuento.setDescripcion(dto.getDescripcion());
            descuento.setPorcentaje(dto.getPorcentaje());
            descuento.setActivo(dto.getActivo());
            return descuentoRepository.save(descuento);
        });
    }

    // Eliminar descuento
    public boolean eliminar(Long id) {
        logger.info("Eliminando descuento con ID: {}", id);
        if (descuentoRepository.existsById(id)) {
            descuentoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}