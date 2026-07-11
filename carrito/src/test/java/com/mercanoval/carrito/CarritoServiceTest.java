package com.mercanoval.carrito;

import com.mercanoval.carrito.dto.CarritoDTO;
import com.mercanoval.carrito.model.Carrito;
import com.mercanoval.carrito.repository.CarritoRepository;
import com.mercanoval.carrito.service.CarritoService;
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
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    // Cadena separada para la verificación de "clientes"
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecCliente;
    @Mock
    private WebClient.ResponseSpec responseSpecCliente;

    // Cadena separada para la verificación de "productos"
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecProducto;
    @Mock
    private WebClient.ResponseSpec responseSpecProducto;

    @InjectMocks
    private CarritoService carritoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(carritoService, "clientesUrl", "http://clientes:8081");
        ReflectionTestUtils.setField(carritoService, "productosUrl", "http://productos:8082");
    }

    private Carrito crearCarritoDeEjemplo() {
        Carrito c = new Carrito();
        c.setId(1L);
        c.setClienteId(5L);
        c.setProductoId(7L);
        c.setCantidad(2);
        c.setPrecioUnitario(999.99);
        c.setTotal(1999.98);
        return c;
    }

    @SuppressWarnings("unchecked")
    private void mockearVerificacionCliente(int statusCode) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains("/api/clientes/"))).thenReturn(requestHeadersSpecCliente);
        when(requestHeadersSpecCliente.retrieve()).thenReturn(responseSpecCliente);
        when(responseSpecCliente.toBodilessEntity()).thenReturn(
                Mono.just(ResponseEntity.status(statusCode).build())
        );
    }

    @SuppressWarnings("unchecked")
    private void mockearVerificacionProducto(int statusCode) {
        when(requestHeadersUriSpec.uri(contains("/api/productos/"))).thenReturn(requestHeadersSpecProducto);
        when(requestHeadersSpecProducto.retrieve()).thenReturn(responseSpecProducto);
        when(responseSpecProducto.toBodilessEntity()).thenReturn(
                Mono.just(ResponseEntity.status(statusCode).build())
        );
    }

    @Test
    void obtenerTodos_debeRetornarListaDeItems() {
        // Given
        when(carritoRepository.findAll()).thenReturn(Arrays.asList(crearCarritoDeEjemplo()));

        // When
        List<Carrito> resultado = carritoService.obtenerTodos();

        // Then
        assertEquals(1, resultado.size());
        verify(carritoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarElItem() {
        // Given
        when(carritoRepository.findById(1L)).thenReturn(Optional.of(crearCarritoDeEjemplo()));

        // When
        Optional<Carrito> resultado = carritoService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getCantidad());
    }

    @Test
    void obtenerPorCliente_debeRetornarLosItemsDeEseCliente() {
        // Given
        when(carritoRepository.findByClienteId(5L)).thenReturn(Arrays.asList(crearCarritoDeEjemplo()));

        // When
        List<Carrito> resultado = carritoService.obtenerPorCliente(5L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(5L, resultado.get(0).getClienteId());
    }

    @Test
    void agregar_cuandoClienteYProductoExisten_debeGuardarYRetornarElItem() {
        // Given
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(5L);
        dto.setProductoId(7L);
        dto.setCantidad(3);
        dto.setPrecioUnitario(500.0);
        dto.setTotal(1500.0);

        mockearVerificacionCliente(200);  // cliente SI existe
        mockearVerificacionProducto(200); // producto SI existe

        Carrito guardado = crearCarritoDeEjemplo();
        guardado.setCantidad(3);
        guardado.setTotal(1500.0);

        when(carritoRepository.save(any(Carrito.class))).thenReturn(guardado);

        // When
        Carrito resultado = carritoService.agregar(dto);

        // Then
        assertNotNull(resultado.getId());
        assertEquals(3, resultado.getCantidad());
        verify(carritoRepository, times(1)).save(any(Carrito.class));
    }

    @Test
    void agregar_cuandoClienteNoExiste_debeLanzarExcepcionYNoVerificarProducto() {
        // Given
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(999L);
        dto.setProductoId(7L);
        dto.setCantidad(1);
        dto.setPrecioUnitario(100.0);
        dto.setTotal(100.0);

        mockearVerificacionCliente(404); // cliente NO existe

        // When / Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> carritoService.agregar(dto));
        assertEquals("Cliente no encontrado", excepcion.getMessage());
        verify(carritoRepository, never()).save(any(Carrito.class));
        // No debe siquiera intentar verificar el producto si el cliente ya falló
        verify(requestHeadersUriSpec, never()).uri(contains("/api/productos/"));
    }

    @Test
    void agregar_cuandoProductoNoExiste_debeLanzarExcepcion() {
        // Given
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(5L);
        dto.setProductoId(999L);
        dto.setCantidad(1);
        dto.setPrecioUnitario(100.0);
        dto.setTotal(100.0);

        mockearVerificacionCliente(200);  // cliente SI existe
        mockearVerificacionProducto(404); // producto NO existe

        // When / Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> carritoService.agregar(dto));
        assertEquals("Producto no encontrado", excepcion.getMessage());
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Carrito existente = crearCarritoDeEjemplo();

        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(5L);
        dto.setProductoId(7L);
        dto.setCantidad(10);
        dto.setPrecioUnitario(999.99);
        dto.setTotal(9999.90);

        when(carritoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Carrito> resultado = carritoService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(10, resultado.get().getCantidad());
        assertEquals(9999.90, resultado.get().getTotal());
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        CarritoDTO dto = new CarritoDTO();
        dto.setClienteId(1L);
        dto.setProductoId(1L);
        dto.setCantidad(1);
        dto.setPrecioUnitario(1.0);
        dto.setTotal(1.0);

        when(carritoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Carrito> resultado = carritoService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(carritoRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = carritoService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(carritoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(carritoRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = carritoService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(carritoRepository, never()).deleteById(anyLong());
    }

    @Test
    void vaciarCarrito_debeLlamarAlMetodoDeleteByClienteId() {
        // When
        carritoService.vaciarCarrito(5L);

        // Then
        verify(carritoRepository, times(1)).deleteByClienteId(5L);
    }
}
