package com.mercanoval.inventario;

import com.mercanoval.inventario.dto.InventarioDTO;
import com.mercanoval.inventario.model.Inventario;
import com.mercanoval.inventario.repository.InventarioRepository;
import com.mercanoval.inventario.service.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private InventarioService inventarioService;

    @BeforeEach
    void setUp() {
        // La URL de productos se inyecta normalmente con @Value desde application.yml,
        // pero en un test unitario sin contexto de Spring hay que setearla a mano.
        ReflectionTestUtils.setField(inventarioService, "productosUrl", "http://productos:8082");
    }

    private Inventario crearInventarioDeEjemplo() {
        Inventario inv = new Inventario();
        inv.setId(1L);
        inv.setProductoId(10L);
        inv.setStock(50);
        inv.setStockMinimo(5);
        inv.setUbicacion("Bodega A");
        return inv;
    }

    @SuppressWarnings("unchecked")
    private void mockearRespuestaWebClient(int statusCode) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(
                Mono.just(ResponseEntity.status(statusCode).build())
        );
    }

    @Test
    void obtenerTodos_debeRetornarListaDeInventarios() {
        // Given
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(crearInventarioDeEjemplo()));

        // When
        List<Inventario> resultado = inventarioService.obtenerTodos();

        // Then
        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarElInventario() {
        // Given
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(crearInventarioDeEjemplo()));

        // When
        Optional<Inventario> resultado = inventarioService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(50, resultado.get().getStock());
    }

    @Test
    void obtenerPorProducto_cuandoExiste_debeRetornarElInventario() {
        // Given
        when(inventarioRepository.findByProductoId(10L)).thenReturn(Optional.of(crearInventarioDeEjemplo()));

        // When
        Optional<Inventario> resultado = inventarioService.obtenerPorProducto(10L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getProductoId());
    }

    @Test
    void obtenerStockBajo_debeRetornarInventariosConStockMenorOIgualA10() {
        // Given
        Inventario stockBajo = crearInventarioDeEjemplo();
        stockBajo.setStock(3);
        when(inventarioRepository.findByStockLessThanEqual(10)).thenReturn(Arrays.asList(stockBajo));

        // When
        List<Inventario> resultado = inventarioService.obtenerStockBajo();

        // Then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getStock() <= 10);
        verify(inventarioRepository, times(1)).findByStockLessThanEqual(10);
    }

    @Test
    void crear_cuandoProductoExiste_debeGuardarYRetornarElInventario() {
        // Given
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(10L);
        dto.setStock(100);
        dto.setStockMinimo(10);
        dto.setUbicacion("Bodega Central");

        mockearRespuestaWebClient(200); // el producto SI existe

        Inventario guardado = new Inventario();
        guardado.setId(5L);
        guardado.setProductoId(10L);
        guardado.setStock(100);
        guardado.setStockMinimo(10);
        guardado.setUbicacion("Bodega Central");

        when(inventarioRepository.save(any(Inventario.class))).thenReturn(guardado);

        // When
        Inventario resultado = inventarioService.crear(dto);

        // Then
        assertNotNull(resultado.getId());
        assertEquals(100, resultado.getStock());
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void crear_cuandoProductoNoExiste_debeLanzarExcepcion() {
        // Given
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(999L);
        dto.setStock(10);
        dto.setStockMinimo(1);
        dto.setUbicacion("Bodega X");

        mockearRespuestaWebClient(404); // el producto NO existe

        // When / Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> inventarioService.crear(dto));
        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Inventario existente = crearInventarioDeEjemplo();

        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(10L);
        dto.setStock(200);
        dto.setStockMinimo(20);
        dto.setUbicacion("Bodega B");

        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Inventario> resultado = inventarioService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(200, resultado.get().getStock());
        assertEquals("Bodega B", resultado.get().getUbicacion());
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(1L);
        dto.setStock(1);
        dto.setStockMinimo(1);
        dto.setUbicacion("X");

        when(inventarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Inventario> resultado = inventarioService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = inventarioService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(inventarioRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = inventarioService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(inventarioRepository, never()).deleteById(anyLong());
    }
}
