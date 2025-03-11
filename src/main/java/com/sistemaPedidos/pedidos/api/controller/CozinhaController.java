package com.sistemaPedidos.pedidos.api.controller;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeEmUsoException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.model.Cozinha;
import com.sistemaPedidos.pedidos.domain.repository.CozinhaRepository;
import com.sistemaPedidos.pedidos.domain.service.CozinhaService;
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

@Tag( name = "Cozinha", description = "API REST Cozinha")
@RestController
@RequestMapping("/cozinhas")
public class CozinhaController {

    @Autowired
    private CozinhaService cozinhaService;

    @Autowired
    private CozinhaRepository cozinhaRepository;


    @Operation(summary = "Lista todas as cozinhas cadastradas.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Cozinha.class))))
    @GetMapping
    public ResponseEntity<?> listar(){
        return ResponseEntity.ok().body(cozinhaService.todas());
    }

    @Operation(summary = "Busca uma cozinha por ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Cozinha.class)))
    @ApiResponse(responseCode = "404", description = "Not Found")
    @GetMapping("/{cozinhaId}")
    public ResponseEntity<?> listarPorId(@Parameter(description = "ID da cozinha que precisa ser buscada.") @PathVariable("cozinhaId") Long id){
        var cozinha = cozinhaRepository.findById(id);

        if (cozinha.isPresent())
            return ResponseEntity.ok().body(cozinha);

        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cadastra uma nova cozinha.")
    @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = Cozinha.class)))
    @PostMapping
    public ResponseEntity<?> salvar( @RequestBody Cozinha cozinha){
        var cozinhaSalva = cozinhaService.salvar(cozinha);

        return ResponseEntity.status(HttpStatus.CREATED).body(cozinhaSalva);
    }

    @Operation(summary = "Atualiza uma cozinha existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Cozinha.class))),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/{cozinhaId}")
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da cozinha que precisa ser atualizada.") @PathVariable("cozinhaId") Long id, @RequestBody Cozinha cozinha){
        var cozinhaAtual = cozinhaRepository.findById(id);

        if (cozinhaAtual.isPresent()){
            BeanUtils.copyProperties(cozinha,cozinhaAtual.get(),"id" );

            return ResponseEntity.ok().body(cozinhaService.salvar(cozinhaAtual.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove uma cozinha existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "409", description = "CONFLICT")
    })
    @DeleteMapping("/{cozinhaId}")
    public ResponseEntity<?> remover(@Parameter(description = "ID da cozinha que precisa ser removido.") @PathVariable("cozinhaId") Long id){

        try {
            cozinhaService.remover(id);
            return ResponseEntity.noContent().build();

        }catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (EntidadeEmUsoException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }

    }


}
