package com.mercanoval.pedidos;

import com.mercanoval.pedidos.dto.PedidoDTO;
import com.mercanoval.pedidos.model.Pedido;
import com.mercanoval.pedidos.repository.PedidoRepository;
import com.mercanoval.pedidos.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private WebClient webClient;

	@InjectMocks
	private PedidoService pedidoService;

	@Test
	void obtenerTodos_debeRetornarListaDePedidos() {
		// Given
		Pedido pedido1 = new Pedido();
		pedido1.setId(1L);
		pedido1.setClienteId(1L);
		pedido1.setProductoId(1L);
		pedido1.setCantidad(2);
		pedido1.setTotal(1999.98);
		pedido1.setEstado("PENDIENTE");

		Pedido pedido2 = new Pedido();
		pedido2.setId(2L);
		pedido2.setClienteId(2L);
		pedido2.setProductoId(2L);
		pedido2.setCantidad(1);
		pedido2.setTotal(29.99);
		pedido2.setEstado("COMPLETADO");

		when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedido1, pedido2));

		// When
		List<Pedido> resultado = pedidoService.obtenerTodos();

		// Then
		assertEquals(2, resultado.size());
		assertEquals("PENDIENTE", resultado.get(0).getEstado());
		verify(pedidoRepository, times(1)).findAll();
	}

	@Test
	void obtenerPorId_debeRetornarPedidoCuandoExiste() {
		// Given
		Pedido pedido = new Pedido();
		pedido.setId(1L);
		pedido.setClienteId(1L);
		pedido.setProductoId(1L);
		pedido.setCantidad(2);
		pedido.setTotal(1999.98);
		pedido.setEstado("PENDIENTE");

		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

		// When
		Optional<Pedido> resultado = pedidoService.obtenerPorId(1L);

		// Then
		assertTrue(resultado.isPresent());
		assertEquals("PENDIENTE", resultado.get().getEstado());
		verify(pedidoRepository, times(1)).findById(1L);
	}

	@Test
	void obtenerPorId_debeRetornarVacioCuandoNoExiste() {
		// Given
		when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

		// When
		Optional<Pedido> resultado = pedidoService.obtenerPorId(99L);

		// Then
		assertFalse(resultado.isPresent());
		verify(pedidoRepository, times(1)).findById(99L);
	}

	@Test
	void obtenerPorCliente_debeRetornarPedidosDelCliente() {
		// Given
		Pedido pedido = new Pedido();
		pedido.setId(1L);
		pedido.setClienteId(1L);
		pedido.setProductoId(1L);
		pedido.setCantidad(2);
		pedido.setTotal(1999.98);
		pedido.setEstado("PENDIENTE");

		when(pedidoRepository.findByClienteId(1L)).thenReturn(Arrays.asList(pedido));

		// When
		List<Pedido> resultado = pedidoService.obtenerPorCliente(1L);

		// Then
		assertEquals(1, resultado.size());
		assertEquals(1L, resultado.get(0).getClienteId());
		verify(pedidoRepository, times(1)).findByClienteId(1L);
	}

	@Test
	void eliminar_debeRetornarTrueCuandoExiste() {
		// Given
		when(pedidoRepository.existsById(1L)).thenReturn(true);
		doNothing().when(pedidoRepository).deleteById(1L);

		// When
		boolean resultado = pedidoService.eliminar(1L);

		// Then
		assertTrue(resultado);
		verify(pedidoRepository, times(1)).deleteById(1L);
	}

	@Test
	void eliminar_debeRetornarFalseCuandoNoExiste() {
		// Given
		when(pedidoRepository.existsById(99L)).thenReturn(false);

		// When
		boolean resultado = pedidoService.eliminar(99L);

		// Then
		assertFalse(resultado);
		verify(pedidoRepository, never()).deleteById(any());
	}
}