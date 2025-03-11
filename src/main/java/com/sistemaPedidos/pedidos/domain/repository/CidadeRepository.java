package com.sistemaPedidos.pedidos.domain.repository;

import com.sistemaPedidos.pedidos.domain.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {
}
