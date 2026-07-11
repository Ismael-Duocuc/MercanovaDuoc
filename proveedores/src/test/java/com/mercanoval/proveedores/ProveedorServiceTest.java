package com.mercanoval.proveedores;

import com.mercanoval.proveedores.dto.ProveedorDTO;
import com.mercanoval.proveedores.model.Proveedor;
import com.mercanoval.proveedores.repository.ProveedorRepository;
import com.mercanoval.proveedores.service.ProveedorService;
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
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor crearProveedorDeEjemplo() {
        Proveedor p = new Proveedor();
        p.setId(1L);
        p.setNombre("Distribuidora Andina");
        p.setEmail("contacto@andina.cl");
        p.setTelefono("+56912345678");
        p.setDireccion("Av. Providencia 1234");
        p.setPais("Chile");
        return p;
    }

    @Test
    void obtenerTodos_debeRetornarListaDeProveedores() {
        // Given
        Proveedor p1 = crearProveedorDeEjemplo();
        Proveedor p2 = new Proveedor();
        p2.setId(2L);
        p2.setNombre("Importadora Sur");
        p2.setPais("Argentina");

        when(proveedorRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        // When
        List<Proveedor> resultado = proveedorService.obtenerTodos();

        // Then
        assertEquals(2, resultado.size());
        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarElProveedor() {
        // Given
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(crearProveedorDeEjemplo()));

        // When
        Optional<Proveedor> resultado = proveedorService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Distribuidora Andina", resultado.get().getNombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarVacio() {
        // Given
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Proveedor> resultado = proveedorService.obtenerPorId(99L);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorPais_debeRetornarSoloLosDeEsePais() {
        // Given
        Proveedor chileno = crearProveedorDeEjemplo();
        when(proveedorRepository.findByPais("Chile")).thenReturn(Arrays.asList(chileno));

        // When
        List<Proveedor> resultado = proveedorService.obtenerPorPais("Chile");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Chile", resultado.get(0).getPais());
        verify(proveedorRepository, times(1)).findByPais("Chile");
    }

    @Test
    void obtenerPorPais_cuandoNoHayCoincidencias_debeRetornarListaVacia() {
        // Given
        when(proveedorRepository.findByPais("Antartida")).thenReturn(List.of());

        // When
        List<Proveedor> resultado = proveedorService.obtenerPorPais("Antartida");

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_debeGuardarYRetornarElProveedor() {
        // Given
        ProveedorDTO dto = new ProveedorDTO();
        dto.setNombre("Tech Import SPA");
        dto.setEmail("ventas@techimport.cl");
        dto.setTelefono("+56987654321");
        dto.setDireccion("Los Militares 5500");
        dto.setPais("Chile");

        Proveedor guardado = new Proveedor();
        guardado.setId(3L);
        guardado.setNombre("Tech Import SPA");
        guardado.setEmail("ventas@techimport.cl");
        guardado.setTelefono("+56987654321");
        guardado.setDireccion("Los Militares 5500");
        guardado.setPais("Chile");

        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(guardado);

        // When
        Proveedor resultado = proveedorService.crear(dto);

        // Then
        assertNotNull(resultado.getId());
        assertEquals("Tech Import SPA", resultado.getNombre());
        assertEquals("ventas@techimport.cl", resultado.getEmail());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Proveedor existente = crearProveedorDeEjemplo();

        ProveedorDTO dto = new ProveedorDTO();
        dto.setNombre("Distribuidora Andina Ltda.");
        dto.setEmail("nuevo@andina.cl");
        dto.setTelefono("+56911112222");
        dto.setDireccion("Nueva dirección 456");
        dto.setPais("Chile");

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Proveedor> resultado = proveedorService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Distribuidora Andina Ltda.", resultado.get().getNombre());
        assertEquals("nuevo@andina.cl", resultado.get().getEmail());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        ProveedorDTO dto = new ProveedorDTO();
        dto.setNombre("X");
        dto.setEmail("x@x.cl");
        dto.setTelefono("123");
        dto.setDireccion("X");
        dto.setPais("X");

        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Proveedor> resultado = proveedorService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(proveedorRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = proveedorService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(proveedorRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(proveedorRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = proveedorService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(proveedorRepository, never()).deleteById(anyLong());
    }
}
