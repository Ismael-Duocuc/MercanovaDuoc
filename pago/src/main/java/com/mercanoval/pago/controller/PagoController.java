package com.mercanoval.pago.controller;

import com.mercanoval.pago.dto.PagoDTO;
import com.mercanoval.pago.model.Pago;
import com.mercanoval.pago.service.PagoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos; verifica pedido y aplica descuentos vía comunicación REST")
public class PagoController {

    private static final Logger logger = LoggerFactory.getLogger(PagoController.class);
    private final PagoService pagoService;

    @Operation(summary = "Listar todos los pagos", description = "Retorna la lista completa de pagos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida correctamente",
                    content = @Content(schema = @Schema(implementation = Pago.class)))
    })
    @GetMapping
    public ResponseEntity<List<Pago>> obtenerTodos() {
        logger.info("GET /api/pagos");
        return ResponseEntity.ok(pagoService.obtenerTodos());
    }

    @Operation(summary = "Obtener pago por ID", description = "Busca y retorna un pago según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago encontrado",
                    content = @Content(schema = @Schema(implementation = Pago.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pago con el ID indicado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPorId(
            @Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        logger.info("GET /api/pagos/{}", id);
        return pagoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar pagos por pedido", description = "Retorna todos los pagos asociados a un pedido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos del pedido",
                    content = @Content(schema = @Schema(implementation = Pago.class)))
    })
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<Pago>> obtenerPorPedido(
            @Parameter(description = "ID del pedido", example = "1") @PathVariable Long pedidoId) {
        logger.info("GET /api/pagos/pedido/{}", pedidoId);
        return ResponseEntity.ok(pagoService.obtenerPorPedido(pedidoId));
    }

    @Operation(summary = "Buscar pagos por estado", description = "Retorna todos los pagos que tienen el estado indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de pagos con ese estado",
                    content = @Content(schema = @Schema(implementation = Pago.class)))
    })
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Pago>> obtenerPorEstado(
            @Parameter(description = "Estado del pago", example = "PENDIENTE") @PathVariable String estado) {
        logger.info("GET /api/pagos/estado/{}", estado);
        return ResponseEntity.ok(pagoService.obtenerPorEstado(estado));
    }

    @Operation(summary = "Crear un nuevo pago",
            description = "Crea un pago verificando previamente que el pedido exista. Si se envía un código de descuento, consulta el microservicio de descuentos y aplica el porcentaje correspondiente al monto final.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pago creado exitosamente",
                    content = @Content(schema = @Schema(implementation = Pago.class),
                            examples = @ExampleObject(value = "{\"id\":1,\"pedidoId\":30,\"monto\":750.0,\"metodoPago\":\"Tarjeta de crédito\",\"estado\":\"PENDIENTE\",\"codigoDescuento\":\"VERANO25\",\"descuentoAplicado\":250.0}"))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, o el pedido asociado no existe", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Pago> crear(@Valid @RequestBody PagoDTO dto) {
        logger.info("POST /api/pagos");
        try {
            return ResponseEntity.status(201).body(pagoService.crear(dto));
        } catch (RuntimeException e) {
            logger.error("Error al crear pago: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Actualizar un pago existente", description = "Actualiza los datos de un pago ya registrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pago actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Pago.class))),
            @ApiResponse(responseCode = "404", description = "No existe un pago con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en el body", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Pago> actualizar(
            @Parameter(description = "ID del pago a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody PagoDTO dto) {
        logger.info("PUT /api/pagos/{}", id);
        return pagoService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un pago", description = "Elimina un pago existente según su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pago eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un pago con el ID indicado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del pago a eliminar", example = "1") @PathVariable Long id) {
        logger.info("DELETE /api/pagos/{}", id);
        return pagoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
