package com.sistemaPedidos.pedidos.domain.service;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeComEsseNomeJaCriadaException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeSemCorpoException;
import com.sistemaPedidos.pedidos.domain.model.Cozinha;
import com.sistemaPedidos.pedidos.domain.model.Restaurante;
import com.sistemaPedidos.pedidos.domain.repository.CozinhaRepository;
import com.sistemaPedidos.pedidos.domain.repository.RestauranteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private CozinhaRepository cozinhaRepository;

    public List<Restaurante> todos() {
        return restauranteRepository.findAll();
    }

    public Restaurante salvar(Restaurante restaurante) {

        // VERIFICAÇOES
        Long idCozinha = restaurante.getCozinha().getId();
        Cozinha cozinha = cozinhaRepository.findById(idCozinha)
                .orElseThrow(()-> new EntidadeNaoEncontradaException(
                        String.format("Não existe cadastro de cozinha com código %d", restaurante.getCozinha().getId())));

        var restauranteNome = restauranteRepository.findByNome(restaurante.getNome());

        if (restauranteNome.isPresent())
            throw  new EntidadeComEsseNomeJaCriadaException("Já existe um restaurante com esse nome.");

        // FIM DA VERIFICAÇOES

        restaurante.setCozinha(cozinha);

        try {
            return restauranteRepository.save(restaurante);

        }catch (HttpMessageNotReadableException e){
            throw new EntidadeSemCorpoException("Não e possivel criar um restaurante sem corpo!");
        }
    }

    public void remover(Long id) {
        if(!restauranteRepository.existsById(id))
            throw new EntidadeNaoEncontradaException(
                    "Não existe uma Restaurante com o ID: %d".formatted(id));

        try {
            restauranteRepository.deleteById(id);

        }catch (IllegalArgumentException e) {
            throw new EntidadeNaoEncontradaException(
                    "Não existe uma cozinha com o ID: %d.".formatted(id));
        }
    }

    public Optional<Restaurante> buscarPorID(Long id) {
        return restauranteRepository.findById(id);
    }
}
