package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Protetico;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProteticoRepository extends JpaRepository<Protetico, Long> {

    // Buscar por registro profissional
    Optional<Protetico> findByRegistroProfissional(String registroProfissional);

    // Buscar por especialização
    List<Protetico> findByEspecializacao(String especializacao);

    // Buscar protéticos que aceitam terceirização
    List<Protetico> findByAceitaTerceirizacaoTrueAndAtivoTrue();

    // Buscar protéticos ativos
    List<Protetico> findByAtivoTrue();
}