package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Dentista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentistaRepository extends JpaRepository<Dentista, Long> {

    // Buscar dentista por CRO
    Optional<Dentista> findByCro(String cro);

    // Buscar por especialidade
    List<Dentista> findByEspecialidade(String especialidade);

    // Buscar dentistas ativos
    List<Dentista> findByAtivoTrue();
}