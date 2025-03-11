package com.sistemaPedidos.pedidos.domain.repository;

import com.sistemaPedidos.pedidos.domain.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    Optional<Restaurante> findByNome(String nome);
}
