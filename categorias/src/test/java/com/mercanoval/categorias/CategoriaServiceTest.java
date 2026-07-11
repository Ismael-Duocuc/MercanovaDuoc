package com.mercanoval.categorias;

import com.mercanoval.categorias.dto.CategoriaDTO;
import com.mercanoval.categorias.model.Categoria;
import com.mercanoval.categorias.repository.CategoriaRepository;
import com.mercanoval.categorias.service.CategoriaService;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void obtenerTodos_debeRetornarListaDeCategorias() {
        // Given
        Categoria c1 = new Categoria();
        c1.setId(1L);
        c1.setNombre("Electrónica");
        c1.setDescripcion("Productos electrónicos");

        Categoria c2 = new Categoria();
        c2.setId(2L);
        c2.setNombre("Ropa");
        c2.setDescripcion("Vestimenta");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        // When
        List<Categoria> resultado = categoriaService.obtenerTodos();

        // Then
        assertEquals(2, resultado.size());
        assertEquals("Electrónica", resultado.get(0).getNombre());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarLaCategoria() {
        // Given
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Electrónica");
        categoria.setDescripcion("Productos electrónicos");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        // When
        Optional<Categoria> resultado = categoriaService.obtenerPorId(1L);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Electrónica", resultado.get().getNombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornarVacio() {
        // Given
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Categoria> resultado = categoriaService.obtenerPorId(99L);

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_debeGuardarYRetornarLaCategoria() {
        // Given
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Hogar");
        dto.setDescripcion("Artículos para el hogar");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setId(1L);
        categoriaGuardada.setNombre("Hogar");
        categoriaGuardada.setDescripcion("Artículos para el hogar");

        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

        // When
        Categoria resultado = categoriaService.crear(dto);

        // Then
        assertNotNull(resultado.getId());
        assertEquals("Hogar", resultado.getNombre());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarLosCampos() {
        // Given
        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setNombre("Electrónica");
        existente.setDescripcion("Descripción vieja");

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("Electrónica y Tecnología");
        dto.setDescripcion("Descripción nueva");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Categoria> resultado = categoriaService.actualizar(1L, dto);

        // Then
        assertTrue(resultado.isPresent());
        assertEquals("Electrónica y Tecnología", resultado.get().getNombre());
        assertEquals("Descripción nueva", resultado.get().getDescripcion());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    void actualizar_cuandoNoExiste_debeRetornarVacio() {
        // Given
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("No importa");
        dto.setDescripcion("No importa");

        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<Categoria> resultado = categoriaService.actualizar(99L, dto);

        // Then
        assertTrue(resultado.isEmpty());
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarYRetornarTrue() {
        // Given
        when(categoriaRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = categoriaService.eliminar(1L);

        // Then
        assertTrue(resultado);
        verify(categoriaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeRetornarFalse() {
        // Given
        when(categoriaRepository.existsById(99L)).thenReturn(false);

        // When
        boolean resultado = categoriaService.eliminar(99L);

        // Then
        assertFalse(resultado);
        verify(categoriaRepository, never()).deleteById(anyLong());
    }
}
