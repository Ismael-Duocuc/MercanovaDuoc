package com.mercanoval.productos;

import com.mercanoval.productos.dto.ProductoDTO;
import com.mercanoval.productos.model.Producto;
import com.mercanoval.productos.repository.ProductoRepository;
import com.mercanoval.productos.service.ProductoService;
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
class ProductoServiceTest {

	@Mock
	private ProductoRepository productoRepository;

	@InjectMocks
	private ProductoService productoService;

	@Test
	void obtenerTodos_debeRetornarListaDeProductos() {
		// Given
		Producto producto1 = new Producto();
		producto1.setId(1L);
		producto1.setNombre("Laptop Dell");
		producto1.setDescripcion("Laptop Dell Inspiron");
		producto1.setPrecio(999.99);
		producto1.setCategoria("Electronica");
		producto1.setProveedor("Dell");

		Producto producto2 = new Producto();
		producto2.setId(2L);
		producto2.setNombre("Mouse Logitech");
		producto2.setDescripcion("Mouse inalámbrico");
		producto2.setPrecio(29.99);
		producto2.setCategoria("Electronica");
		producto2.setProveedor("Logitech");

		when(productoRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));

		// When
		List<Producto> resultado = productoService.obtenerTodos();

		// Then
		assertEquals(2, resultado.size());
		assertEquals("Laptop Dell", resultado.get(0).getNombre());
		verify(productoRepository, times(1)).findAll();
	}

	@Test
	void obtenerPorId_debeRetornarProductoCuandoExiste() {
		// Given
		Producto producto = new Producto();
		producto.setId(1L);
		producto.setNombre("Laptop Dell");
		producto.setDescripcion("Laptop Dell Inspiron");
		producto.setPrecio(999.99);
		producto.setCategoria("Electronica");
		producto.setProveedor("Dell");

		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

		// When
		Optional<Producto> resultado = productoService.obtenerPorId(1L);

		// Then
		assertTrue(resultado.isPresent());
		assertEquals("Laptop Dell", resultado.get().getNombre());
		verify(productoRepository, times(1)).findById(1L);
	}

	@Test
	void obtenerPorId_debeRetornarVacioCuandoNoExiste() {
		// Given
		when(productoRepository.findById(99L)).thenReturn(Optional.empty());

		// When
		Optional<Producto> resultado = productoService.obtenerPorId(99L);

		// Then
		assertFalse(resultado.isPresent());
		verify(productoRepository, times(1)).findById(99L);
	}

	@Test
	void crear_debeGuardarYRetornarProducto() {
		// Given
		ProductoDTO dto = new ProductoDTO();
		dto.setNombre("Laptop Dell");
		dto.setDescripcion("Laptop Dell Inspiron");
		dto.setPrecio(999.99);
		dto.setCategoria("Electronica");
		dto.setProveedor("Dell");

		Producto productoGuardado = new Producto();
		productoGuardado.setId(1L);
		productoGuardado.setNombre(dto.getNombre());
		productoGuardado.setDescripcion(dto.getDescripcion());
		productoGuardado.setPrecio(dto.getPrecio());
		productoGuardado.setCategoria(dto.getCategoria());
		productoGuardado.setProveedor(dto.getProveedor());

		when(productoRepository.save(any(Producto.class))).thenReturn(productoGuardado);

		// When
		Producto resultado = productoService.crear(dto);

		// Then
		assertNotNull(resultado);
		assertEquals("Laptop Dell", resultado.getNombre());
		assertEquals(999.99, resultado.getPrecio());
		verify(productoRepository, times(1)).save(any(Producto.class));
	}

	@Test
	void eliminar_debeRetornarTrueCuandoExiste() {
		// Given
		when(productoRepository.existsById(1L)).thenReturn(true);
		doNothing().when(productoRepository).deleteById(1L);

		// When
		boolean resultado = productoService.eliminar(1L);

		// Then
		assertTrue(resultado);
		verify(productoRepository, times(1)).deleteById(1L);
	}

	@Test
	void eliminar_debeRetornarFalseCuandoNoExiste() {
		// Given
		when(productoRepository.existsById(99L)).thenReturn(false);

		// When
		boolean resultado = productoService.eliminar(99L);

		// Then
		assertFalse(resultado);
		verify(productoRepository, never()).deleteById(any());
	}
}