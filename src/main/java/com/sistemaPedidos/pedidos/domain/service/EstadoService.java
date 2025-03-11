package com.sistemaPedidos.pedidos.domain.service;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeEmUsoException;
import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.model.Estado;
import com.sistemaPedidos.pedidos.domain.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository estadoRepository;


    public List<Estado> todos(){
        return estadoRepository.findAll();
    }

    public Optional<Estado> buscarPorId(Long id) {
        return estadoRepository.findById(id);
    }

    public Estado salvar(Estado estado) {

        return estadoRepository.save(estado);
    }

    public void remover(Long id) {
        if(!estadoRepository.existsById(id))
            throw new EntidadeNaoEncontradaException(
                    "Não existe um estado com o ID: %d".formatted(id));

        try {
            estadoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntidadeEmUsoException(
                    "Estado de código %d não pode ser removida, pois está em uso".formatted(id));
        }
    }
}
