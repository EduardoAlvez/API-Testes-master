package com.sistemaPedidos.pedidos.api.controller;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.model.Cidade;
import com.sistemaPedidos.pedidos.domain.service.CidadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag( name = "Cidade", description = "API REST Cidade")
@RestController
@RequestMapping("/cidades")
public class CidadeController {

    @Autowired
    CidadeService cidadeService;


    @Operation(summary = "Lista todas as cidades cadastradas.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", array = @ArraySchema(schema = @Schema(implementation = Cidade.class))))
    @GetMapping
    public ResponseEntity<?> listar(){
        var cidades = cidadeService.todas();

        return ResponseEntity.status(HttpStatus.OK).body(cidades);
    }

    @Operation(summary = "Busca uma cidade por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Cidade.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{cidadeId}")
    public ResponseEntity<?> buscarPorId(@Parameter(description = "ID da cidade que precisa ser buscado.") @PathVariable("cidadeId") Long id){
        var cidade = cidadeService.buscarPorId(id);

        if (cidade.isPresent())
            return ResponseEntity.status(HttpStatus.OK).body(cidade);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Cadastra uma nova cidade.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = Cidade.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Cidade cidade){
        var cidadeSalva = cidadeService.salvar(cidade);

        return ResponseEntity.status(HttpStatus.CREATED).body(cidadeSalva);
    }

    @Operation(summary = "Atualiza uma cidade existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Cidade.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PutMapping("/{cidadeId}")
    public ResponseEntity<?> atualizar(@Parameter(description = "ID da cidade que precisa ser atualizada.") @PathVariable Long cidadeId, @RequestBody Cidade cidade){
        var cidadeAtual = cidadeService.buscarPorId(cidadeId);

        if (cidadeAtual.isPresent()){
            BeanUtils.copyProperties(cidade,cidadeAtual.get(),"id" );

            try{
                return ResponseEntity.status(HttpStatus.OK).body(cidadeService.salvar(cidadeAtual.get()));

            }catch (EntidadeNaoEncontradaException e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Remove uma cidade existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{cidadeId}")
    public ResponseEntity<?> remover(@Parameter(description = "ID da cidade que precisa ser removido.") @PathVariable Long cidadeId){
        try {
            cidadeService.remover(cidadeId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



}
