package com.mercanoval.pago;

import com.mercanoval.pago.dto.PagoDTO;
import com.mercanoval.pago.model.Pago;
import com.mercanoval.pago.repository.PagoRepository;
import com.mercanoval.pago.service.PagoService;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    // Cadena para la verificación del pedido (toBodilessEntity)
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecPedido;
    @Mock
    private WebClient.ResponseSpec responseSpecPedido;

    // Cadena para la consulta del descuento (bodyToMono)
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecDescuento;
    @Mock
    private WebClient.ResponseSpec responseSpecDescuento;

    @InjectMocks
    private PagoService pagoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(pagoService, "pedidosUrl", "http://pedidos:8086");
        ReflectionTestUtils.setField(pagoService, "descuentosUrl", "http://descuentos:8085");
    }

    private Pago crearPagoDeEjemplo() {
        Pago p = new Pago();
        p.setId(1L);
        p.setPedidoId(30L);
        p.setMonto(1000.0);
        p.setMetodoPago("Tarjeta de crédito");
        p.setEstado("PENDIENTE");
        p.setDescuentoAplicado(0.0);
        return p;
    }

    @SuppressWarnings("unchecked")
    private void mockearVerificacionPedido(int statusCode) {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(contains("/api/pedidos/"))).thenReturn(requestHeadersSpecPedido);
        when(requestHeadersSpecPedido.retrieve()).thenReturn(responseSpecPedido);
        when(responseSpecPedido.toBodilessEntity()).thenReturn(
                Mono.just(ResponseEntity.status(statusCode).build())
        );
    }

    @SuppressWarnings("unchecked")
    private void mockearConsultaDescuento(Map<String, Object> respuestaDescuento) {
        when(requestHeadersUriSpec.uri(contains("/api/descuentos/codigo/"))).thenReturn(requestHeadersSpecDescuento);
        when(requestHeadersSpecDescuento.retrieve()).thenReturn(responseSpecDescuento);
        when(responseSpecDescuento.bodyToMono(Map.class)).thenReturn(Mono.just(respuestaDescuento));
    }

    @Test
    void obtenerTodos_debeRetornarListaDePagos() {
        // Given
        when(pagoRepository.findAll()).thenReturn(Arrays.asList(crearPagoDeEjemplo()));

        // When
        List<Pago> resultado = pagoService.obtenerTodos();

        // Then
        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarElPago() {
        // Given
        when(pagoRepository.findById(1L)).thenReturn(Optional.of(crearPagoDeEjemplo()));

        // When
        Optional<Pago> resultado = pagoService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(1000.0, resultado.get().getMonto());
    }

    @Test
    void obtenerPorPedido_debeRetornarLosPagosDeEsePedido() {
        // Given
        when(pagoRepository.findByPedidoId(30L)).thenReturn(Arrays.asList(crearPagoDeEjemplo()));

        // When
        List<Pago> resultado = pagoService.obtenerPorPedido(30L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(30L, resultado.get(0).getPedidoId());
    }

    @Test
    void obtenerPorEstado_debeRetornarLosPagosConEseEstado() {
        // Given
        when(pagoRepository.findByEstado("PENDIENTE")).thenReturn(Arrays.asList(crearPagoDeEjemplo()));

        // When
        List<Pago> resultado = pagoService.obtenerPorEstado("PENDIENTE");

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void crear_sinCodigoDescuento_debeCrearPagoConMontoCompleto() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(30L);
        dto.setMonto(1000.0);
        dto.setMetodoPago("Tarjeta de crédito");
        dto.setEstado("PENDIENTE");
        dto.setCodigoDescuento(null); // sin descuento

        mockearVerificacionPedido(200);

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pago resultado = pagoService.crear(dto);

        // Then
        assertEquals(1000.0, resultado.getMonto());
        assertEquals(0.0, resultado.getDescuentoAplicado());
        assertNull(resultado.getCodigoDescuento());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void crear_conCodigoDescuentoValidoYActivo_debeAplicarElDescuento() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(30L);
        dto.setMonto(1000.0);
        dto.setMetodoPago("Tarjeta de crédito");
        dto.setEstado("PENDIENTE");
        dto.setCodigoDescuento("VERANO25");

        mockearVerificacionPedido(200);
        mockearConsultaDescuento(Map.of("activo", true, "porcentaje", 25.0));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pago resultado = pagoService.crear(dto);

        // Then: 1000 - 25% = 750, descuento aplicado = 250
        assertEquals(750.0, resultado.getMonto());
        assertEquals(250.0, resultado.getDescuentoAplicado());
        assertEquals("VERANO25", resultado.getCodigoDescuento());
    }

    @Test
    void crear_conCodigoDescuentoInactivo_noDebeAplicarElDescuento() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(30L);
        dto.setMonto(1000.0);
        dto.setMetodoPago("Tarjeta de crédito");
        dto.setEstado("PENDIENTE");
        dto.setCodigoDescuento("EXPIRADO10");

        mockearVerificacionPedido(200);
        mockearConsultaDescuento(Map.of("activo", false, "porcentaje", 10.0));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pago resultado = pagoService.crear(dto);

        // Then: el descuento existe pero está inactivo, no se aplica
        assertEquals(1000.0, resultado.getMonto());
        assertEquals(0.0, resultado.getDescuentoAplicado());
    }

    @Test
    void crear_cuandoPedidoNoExiste_debeLanzarExcepcion() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(999L);
        dto.setMonto(100.0);
        dto.setMetodoPago("Efectivo");
        dto.setEstado("PENDIENTE");

        mockearVerificacionPedido(404); // pedido NO existe

        // When / Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> pagoService.crear(dto));
        assertEquals("Pedido no encontrado", excepcion.getMessage());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Pago existente = crearPagoDeEjemplo();

        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(30L);
        dto.setMonto(2000.0);
        dto.setMetodoPago("Transferencia");
        dto.setEstado("PAGADO");
        dto.setCodigoDescuento(null);

        when(pagoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Pago> resultado = pagoService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals(2000.0, resultado.get().getMonto());
        assertEquals("PAGADO", resultado.get().getEstado());
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        PagoDTO dto = new PagoDTO();
        dto.setPedidoId(1L);
        dto.setMonto(1.0);
        dto.setMetodoPago("X");
        dto.setEstado("X");

        when(pagoRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Pago> resultado = pagoService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(pagoRepository, never()).save(any(Pago.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(pagoRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = pagoService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(pagoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(pagoRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = pagoService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(pagoRepository, never()).deleteById(anyLong());
    }
}
