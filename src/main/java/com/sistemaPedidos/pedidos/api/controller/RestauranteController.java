package com.sistemaPedidos.pedidos.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeComEsseNomeJaCriadaException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeSemCorpoException;
import com.sistemaPedidos.pedidos.domain.model.Estado;
import com.sistemaPedidos.pedidos.domain.model.Restaurante;
import com.sistemaPedidos.pedidos.domain.service.RestauranteService;
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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;

@Tag(name = "Restaurante", description = "API REST Restaurante")
@RestController
@RequestMapping("/restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;


    @Operation(summary = "Lista todas os restaurantes cadastradas.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", array = @ArraySchema(schema = @Schema(implementation = Restaurante.class))))
    @GetMapping
    public ResponseEntity<?> listar(){
        var restaurantes = restauranteService.todos();

        return ResponseEntity.ok().body(restaurantes);
    }

    @Operation(summary = "Busca um restaurante por ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{restauranteId}")
    public ResponseEntity<?> buscarPorId(@Parameter(description = "ID do restaurante que precisa ser buscada.") @PathVariable("restauranteId") Long id){
        var restaurante = restauranteService.buscarPorID(id);

        if (restaurante.isPresent())
            return ResponseEntity.ok().body(restaurante);

        return ResponseEntity.notFound().build();
    }


    @Operation(summary = "Cadastra um novo restaurante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
    })
    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody Restaurante restaurante){
        try {
            var restauranteSalva = restauranteService.salvar(restaurante);
            return ResponseEntity.status(HttpStatus.CREATED).body(restauranteSalva);

        }catch (EntidadeNaoEncontradaException | EntidadeSemCorpoException | EntidadeComEsseNomeJaCriadaException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(summary = "Atualiza um restaurante existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PutMapping("/{restauranteId}")
    public ResponseEntity<?> atualizar(@Parameter(description = "ID do restaurante que precisa ser atualizado.") @PathVariable("restauranteId") Long id, @RequestBody Restaurante restaurante){
        var restauranteAtual = restauranteService.buscarPorID(id);

        if (restauranteAtual.isPresent()){
            BeanUtils.copyProperties(restaurante, restauranteAtual.get(),"id" );

            try {
                return ResponseEntity.ok().body(restauranteService.salvar(restauranteAtual.get()));

            }catch (EntidadeComEsseNomeJaCriadaException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Remove um restaurante existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @DeleteMapping("/{restauranteId}")
    public ResponseEntity<?> remover(@Parameter(description = "ID do restaurante que precisa ser removido.") @PathVariable("restauranteId") Long id){
        try {
            restauranteService.remover(id);
            return ResponseEntity.noContent().build();

        }catch (EntidadeNaoEncontradaException e){
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Atualiza parcialmente um restaurante existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "Application/json", schema = @Schema(implementation = Restaurante.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping("/{restauranteId}")
    public ResponseEntity<?> atualizarParcial(@PathVariable("restauranteId") Long id, @RequestBody Map<String, Object> restauranteDados){
        //BUSCA O RESTAURANTE ORIGIAL
        var restaurantePraAtualizar = restauranteService.buscarPorID(id);

        // Verifica se tem algum objeto, caso nao joga uma msg de erro
        if (!restaurantePraAtualizar.isPresent())
            return ResponseEntity.notFound().build();

        mesclar(restauranteDados, restaurantePraAtualizar);
        return atualizar(id, restaurantePraAtualizar.get());

    }

    private void mesclar(Map<String, Object> restauranteDados, Optional<Restaurante> restaurantePraAtualizar) {
        //MAPEA AS VARIAVES E CONVERTE OS DADOS PARA SE IGUAL A DA CLASSE "RESTAURANTE"
        ObjectMapper mapper = new ObjectMapper();
        Restaurante restaurante = mapper.convertValue(restauranteDados, Restaurante.class);

        restauranteDados.forEach((nome, valor) -> {
            //BUSCA NA CLASSE "RESTAURANTE" UMA VARIVEL DE MESMO NOME QUE FOI PASSADA
            Field field = ReflectionUtils.findField(Restaurante.class, nome);
            field.setAccessible(true);

            //BUSCA UM VALOR IQUAL AO QUE FOI PASSADO
            var novoValor = ReflectionUtils.getField(field, restaurante);

            //PARA CADA CAMPO BUSCADO ELE TROCA PELO DADO NOVO QUE FOI ALTERADO
            ReflectionUtils.setField(field, restaurantePraAtualizar, novoValor);
        });
    }


}
