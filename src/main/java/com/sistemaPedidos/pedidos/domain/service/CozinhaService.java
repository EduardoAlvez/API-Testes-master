package com.sistemaPedidos.pedidos.domain.service;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeEmUsoException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.model.Cozinha;
import com.sistemaPedidos.pedidos.domain.repository.CozinhaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CozinhaService {

    @Autowired
    private CozinhaRepository cozinhaRepository;

    public List<Cozinha> todas() {
        return cozinhaRepository.findAll();
    }


    public Cozinha salvar(Cozinha cozinha) {
        return cozinhaRepository.save(cozinha);
    }

    public void remover(Long id) {
        if (!cozinhaRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException(
                    "Não existe uma cozinha com o ID: %d.".formatted(id));
        }

        try {
            cozinhaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeEmUsoException(
                    "Cozinha de código %d não pode ser removida, pois está em uso.".formatted(id));
        }
    }
}
