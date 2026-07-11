package com.mercanoval.envios;

import com.mercanoval.envios.dto.EnvioDTO;
import com.mercanoval.envios.model.Envio;
import com.mercanoval.envios.repository.EnvioRepository;
import com.mercanoval.envios.service.EnvioService;
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
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private EnvioService envioService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(envioService, "pedidosUrl", "http://pedidos:8086");
    }

    private Envio crearEnvioDeEjemplo() {
        Envio e = new Envio();
        e.setId(1L);
        e.setPedidoId(20L);
        e.setDireccionDestino("Av. Kennedy 5000, Santiago");
        e.setEstado("PENDIENTE");
        e.setTransportista("Chilexpress");
        return e;
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
    void obtenerTodos_debeRetornarListaDeEnvios() {
        // Given
        when(envioRepository.findAll()).thenReturn(Arrays.asList(crearEnvioDeEjemplo()));

        // When
        List<Envio> resultado = envioService.obtenerTodos();

        // Then
        assertEquals(1, resultado.size());
        verify(envioRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarElEnvio() {
        // Given
        when(envioRepository.findById(1L)).thenReturn(Optional.of(crearEnvioDeEjemplo()));

        // When
        Optional<Envio> resultado = envioService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Chilexpress", resultado.get().getTransportista());
    }

    @Test
    void obtenerPorPedido_debeRetornarLosEnviosDeEsePedido() {
        // Given
        when(envioRepository.findByPedidoId(20L)).thenReturn(Arrays.asList(crearEnvioDeEjemplo()));

        // When
        List<Envio> resultado = envioService.obtenerPorPedido(20L);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(20L, resultado.get(0).getPedidoId());
    }

    @Test
    void obtenerPorEstado_debeRetornarLosEnviosConEseEstado() {
        // Given
        when(envioRepository.findByEstado("PENDIENTE")).thenReturn(Arrays.asList(crearEnvioDeEjemplo()));

        // When
        List<Envio> resultado = envioService.obtenerPorEstado("PENDIENTE");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
    }

    @Test
    void crear_cuandoPedidoExiste_debeGuardarYRetornarElEnvio() {
        // Given
        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(20L);
        dto.setDireccionDestino("Los Leones 1200, Providencia");
        dto.setEstado("PENDIENTE");
        dto.setTransportista("Correos de Chile");

        mockearRespuestaWebClient(200); // el pedido SI existe

        Envio guardado = new Envio();
        guardado.setId(2L);
        guardado.setPedidoId(20L);
        guardado.setDireccionDestino("Los Leones 1200, Providencia");
        guardado.setEstado("PENDIENTE");
        guardado.setTransportista("Correos de Chile");

        when(envioRepository.save(any(Envio.class))).thenReturn(guardado);

        // When
        Envio resultado = envioService.crear(dto);

        // Then
        assertNotNull(resultado.getId());
        assertEquals("Correos de Chile", resultado.getTransportista());
        verify(envioRepository, times(1)).save(any(Envio.class));
    }

    @Test
    void crear_cuandoPedidoNoExiste_debeLanzarExcepcion() {
        // Given
        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(999L);
        dto.setDireccionDestino("X");
        dto.setEstado("PENDIENTE");
        dto.setTransportista("X");

        mockearRespuestaWebClient(404); // el pedido NO existe

        // When / Then
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> envioService.crear(dto));
        assertEquals("Pedido no encontrado", excepcion.getMessage());
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Envio existente = crearEnvioDeEjemplo();

        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(20L);
        dto.setDireccionDestino("Nueva dirección 999");
        dto.setEstado("ENTREGADO");
        dto.setTransportista("Blue Express");

        when(envioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(envioRepository.save(any(Envio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Envio> resultado = envioService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("ENTREGADO", resultado.get().getEstado());
        assertEquals("Blue Express", resultado.get().getTransportista());
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        EnvioDTO dto = new EnvioDTO();
        dto.setPedidoId(1L);
        dto.setDireccionDestino("X");
        dto.setEstado("X");
        dto.setTransportista("X");

        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Envio> resultado = envioService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(envioRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = envioService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(envioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(envioRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = envioService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(envioRepository, never()).deleteById(anyLong());
    }
}
