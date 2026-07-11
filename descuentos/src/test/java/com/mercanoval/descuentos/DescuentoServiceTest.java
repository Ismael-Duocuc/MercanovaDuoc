package com.mercanoval.descuentos;

import com.mercanoval.descuentos.dto.DescuentoDTO;
import com.mercanoval.descuentos.model.Descuento;
import com.mercanoval.descuentos.repository.DescuentoRepository;
import com.mercanoval.descuentos.service.DescuentoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DescuentoServiceTest {

    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private DescuentoService descuentoService;

    private Descuento crearDescuentoDeEjemplo() {
        Descuento d = new Descuento();
        d.setId(1L);
        d.setCodigo("VERANO25");
        d.setDescripcion("Descuento de verano");
        d.setPorcentaje(25.0);
        d.setActivo(true);
        return d;
    }

    @Test
    void obtenerTodos_debeRetornarListaDeDescuentos() {
        // Given
        Descuento d1 = crearDescuentoDeEjemplo();
        Descuento d2 = new Descuento();
        d2.setId(2L);
        d2.setCodigo("INVIERNO10");
        d2.setPorcentaje(10.0);
        d2.setActivo(false);

        when(descuentoRepository.findAll()).thenReturn(Arrays.asList(d1, d2));

        // When
        List<Descuento> resultado = descuentoService.obtenerTodos();

        // Then
        assertEquals(2, resultado.size());
        verify(descuentoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarElDescuento() {
        // Given
        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(crearDescuentoDeEjemplo()));

        // When
        Optional<Descuento> resultado = descuentoService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("VERANO25", resultado.get().getCodigo());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarVacio() {
        // Given
        when(descuentoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Descuento> resultado = descuentoService.obtenerPorId(99L);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorCodigo_cuandoExiste_debeRetornarElDescuento() {
        // Given
        when(descuentoRepository.findByCodigo("VERANO25")).thenReturn(Optional.of(crearDescuentoDeEjemplo()));

        // When
        Optional<Descuento> resultado = descuentoService.obtenerPorCodigo("VERANO25");

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(25.0, resultado.get().getPorcentaje());
    }

    @Test
    void obtenerPorCodigo_cuandoNoExiste_debeRetornarVacio() {
        // Given
        when(descuentoRepository.findByCodigo("NOEXISTE")).thenReturn(Optional.empty());

        // When
        Optional<Descuento> resultado = descuentoService.obtenerPorCodigo("NOEXISTE");

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerActivos_debeRetornarSoloLosActivos() {
        // Given
        Descuento activo = crearDescuentoDeEjemplo();
        when(descuentoRepository.findByActivo(true)).thenReturn(Arrays.asList(activo));

        // When
        List<Descuento> resultado = descuentoService.obtenerActivos();

        // Then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(descuentoRepository, times(1)).findByActivo(true);
    }

    @Test
    void crear_debeGuardarYRetornarElDescuento() {
        // Given
        DescuentoDTO dto = new DescuentoDTO();
        dto.setCodigo("BLACKFRIDAY");
        dto.setDescripcion("Black Friday");
        dto.setPorcentaje(50.0);
        dto.setActivo(true);

        Descuento guardado = new Descuento();
        guardado.setId(3L);
        guardado.setCodigo("BLACKFRIDAY");
        guardado.setDescripcion("Black Friday");
        guardado.setPorcentaje(50.0);
        guardado.setActivo(true);

        when(descuentoRepository.save(any(Descuento.class))).thenReturn(guardado);

        // When
        Descuento resultado = descuentoService.crear(dto);

        // Then
        assertNotNull(resultado.getId());
        assertEquals("BLACKFRIDAY", resultado.getCodigo());
        assertEquals(50.0, resultado.getPorcentaje());
        verify(descuentoRepository, times(1)).save(any(Descuento.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Descuento existente = crearDescuentoDeEjemplo();

        DescuentoDTO dto = new DescuentoDTO();
        dto.setCodigo("VERANO30");
        dto.setDescripcion("Verano extendido");
        dto.setPorcentaje(30.0);
        dto.setActivo(false);

        when(descuentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(descuentoRepository.save(any(Descuento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Descuento> resultado = descuentoService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("VERANO30", resultado.get().getCodigo());
        assertEquals(30.0, resultado.get().getPorcentaje());
        assertFalse(resultado.get().getActivo());
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        DescuentoDTO dto = new DescuentoDTO();
        dto.setCodigo("X");
        dto.setDescripcion("X");
        dto.setPorcentaje(10.0);
        dto.setActivo(true);

        when(descuentoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Descuento> resultado = descuentoService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(descuentoRepository, never()).save(any(Descuento.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(descuentoRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = descuentoService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(descuentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(descuentoRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = descuentoService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(descuentoRepository, never()).deleteById(anyLong());
    }
}
