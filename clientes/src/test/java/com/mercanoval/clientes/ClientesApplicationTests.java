package com.mercanoval.clientes;

import com.mercanoval.clientes.dto.ClienteDTO;
import com.mercanoval.clientes.model.Cliente;
import com.mercanoval.clientes.repository.ClienteRepository;
import com.mercanoval.clientes.service.ClienteService;
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
class ClienteServiceTest {

	@Mock
	private ClienteRepository clienteRepository;

	@InjectMocks
	private ClienteService clienteService;

	@Test
	void obtenerTodos_debeRetornarListaDeClientes() {
		// Given
		Cliente cliente1 = new Cliente();
		cliente1.setId(1L);
		cliente1.setNombre("Juan Pérez");
		cliente1.setEmail("juan@gmail.com");
		cliente1.setTelefono("912345678");
		cliente1.setDireccion("Av. Siempre Viva 123");

		Cliente cliente2 = new Cliente();
		cliente2.setId(2L);
		cliente2.setNombre("María López");
		cliente2.setEmail("maria@gmail.com");
		cliente2.setTelefono("987654321");
		cliente2.setDireccion("Calle Falsa 456");

		when(clienteRepository.findAll()).thenReturn(Arrays.asList(cliente1, cliente2));

		// When
		List<Cliente> resultado = clienteService.obtenerTodos();

		// Then
		assertEquals(2, resultado.size());
		assertEquals("Juan Pérez", resultado.get(0).getNombre());
		verify(clienteRepository, times(1)).findAll();
	}

	@Test
	void obtenerPorId_debeRetornarClienteCuandoExiste() {
		// Given
		Cliente cliente = new Cliente();
		cliente.setId(1L);
		cliente.setNombre("Juan Pérez");
		cliente.setEmail("juan@gmail.com");
		cliente.setTelefono("912345678");
		cliente.setDireccion("Av. Siempre Viva 123");

		when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

		// When
		Optional<Cliente> resultado = clienteService.obtenerPorId(1L);

		// Then
		assertTrue(resultado.isPresent());
		assertEquals("Juan Pérez", resultado.get().getNombre());
		verify(clienteRepository, times(1)).findById(1L);
	}

	@Test
	void obtenerPorId_debeRetornarVacioCuandoNoExiste() {
		// Given
		when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

		// When
		Optional<Cliente> resultado = clienteService.obtenerPorId(99L);

		// Then
		assertFalse(resultado.isPresent());
		verify(clienteRepository, times(1)).findById(99L);
	}

	@Test
	void crear_debeGuardarYRetornarCliente() {
		// Given
		ClienteDTO dto = new ClienteDTO();
		dto.setNombre("Juan Pérez");
		dto.setEmail("juan@gmail.com");
		dto.setTelefono("912345678");
		dto.setDireccion("Av. Siempre Viva 123");

		Cliente clienteGuardado = new Cliente();
		clienteGuardado.setId(1L);
		clienteGuardado.setNombre(dto.getNombre());
		clienteGuardado.setEmail(dto.getEmail());
		clienteGuardado.setTelefono(dto.getTelefono());
		clienteGuardado.setDireccion(dto.getDireccion());

		when(clienteRepository.save(any(Cliente.class))).thenReturn(clienteGuardado);

		// When
		Cliente resultado = clienteService.crear(dto);

		// Then
		assertNotNull(resultado);
		assertEquals("Juan Pérez", resultado.getNombre());
		assertEquals("juan@gmail.com", resultado.getEmail());
		verify(clienteRepository, times(1)).save(any(Cliente.class));
	}

	@Test
	void eliminar_debeRetornarTrueCuandoExiste() {
		// Given
		when(clienteRepository.existsById(1L)).thenReturn(true);
		doNothing().when(clienteRepository).deleteById(1L);

		// When
		boolean resultado = clienteService.eliminar(1L);

		// Then
		assertTrue(resultado);
		verify(clienteRepository, times(1)).deleteById(1L);
	}

	@Test
	void eliminar_debeRetornarFalseCuandoNoExiste() {
		// Given
		when(clienteRepository.existsById(99L)).thenReturn(false);

		// When
		boolean resultado = clienteService.eliminar(99L);

		// Then
		assertFalse(resultado);
		verify(clienteRepository, never()).deleteById(any());
	}
}