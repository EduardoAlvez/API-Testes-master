package com.sistemaPedidos.pedidos.domain.service;

import com.sistemaPedidos.pedidos.domain.exception.EntidadeNaoEncontradaException;
import com.sistemaPedidos.pedidos.domain.model.Cidade;
import com.sistemaPedidos.pedidos.domain.model.Estado;
import com.sistemaPedidos.pedidos.domain.repository.CidadeRepository;
import com.sistemaPedidos.pedidos.domain.repository.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CidadeService {

    @Autowired
    private CidadeRepository cidadeRepository;
    @Autowired
    private EstadoRepository estadoRepository;


    public List<Cidade> todas(){
        return cidadeRepository.findAll();
    }

    public Optional<Cidade> buscarPorId(Long id){

        return cidadeRepository.findById(id);
    }

    // SALVAR E ATUALIZAR
    public Cidade salvar(Cidade cidade) {
        // USADO PRA ADICIONAR E PARA ATUALIZAR, POIS USA O MERGE.
        Estado estado = estadoRepository.findById(cidade.getEstado().getId())
                .orElseThrow(()-> new EntidadeNaoEncontradaException(String.format("Não existe cadastro de estado com código %d", cidade.getEstado().getId())));

        cidade.setEstado(estado);
        return cidadeRepository.save(cidade);
    }

    public void remover(long id) {
        if(!cidadeRepository.existsById(id))
            throw new EntidadeNaoEncontradaException("Cidade de id %d não encontrado.".formatted(id));

        try {
            cidadeRepository.deleteById(id);
        } catch (EmptyResultDataAccessException | IllegalArgumentException e) {
            throw new EntidadeNaoEncontradaException("Cidade de id %d não encontrado.".formatted(id));
        }
    }


}
