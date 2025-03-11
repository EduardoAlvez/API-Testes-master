package com.sistemaPedidos.pedidos.api.controller;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeEmUsoException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.model.Estado;
import com.sistemaPedidos.pedidos.domain.service.EstadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Estado", description = "API REST Estado")
@RestController
@RequestMapping("/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;


    /**
     * Retrieves a list of all "Estado" entities.
     *
     * @return a ResponseEntity containing the list of all "Estado" entities with HTTP status 200 (OK).
     */
    @Operation(summary = "Lista todas os estados cadastradas.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", array = @ArraySchema(schema = @Schema(implementation = Estado.class))))
    @GetMapping
    public ResponseEntity<?> listar(){
        var estados = estadoService.todos();
        return ResponseEntity.status(HttpStatus.OK).body(estados);
    }

    @Operation(summary = "Busca um estado por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Estado.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{estadoId}")
    public ResponseEntity<?> buscarPorId(@Parameter(description = "ID do estado que precisa ser buscado.") @PathVariable("estadoId") Long id){
        var estado = estadoService.buscarPorId(id);

        if (estado.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(estado);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Cadastra um novo estado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Estado.class))),
    })
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Estado estado){
        var estadoSalva = estadoService.salvar(estado);

        return ResponseEntity.status(HttpStatus.CREATED).body(estadoSalva);

    }

    @Operation(summary = "Atualiza um estado existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Estado.class))),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/{estadoId}")
    public ResponseEntity<?> atualizar(@Parameter(description = "ID do estado que precisa ser atualizado.") @PathVariable("estadoId") Long id, @RequestBody Estado estado){
        var estadoAtual = estadoService.buscarPorId(id);

        if (estadoAtual.isPresent()){
            BeanUtils.copyProperties(estado,estadoAtual.get(),"id" );

            return ResponseEntity.ok().body(estadoService.salvar(estadoAtual.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove um estado existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "409", description = "CONFLICT")
    })
    @DeleteMapping
    public ResponseEntity<?> remover(@Parameter(description = "ID do estado que precisa ser removido.") @PathVariable("estadoId") Long id){

        try {
            estadoService.remover(id);
            return ResponseEntity.noContent().build();

        }catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (EntidadeEmUsoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }
}
