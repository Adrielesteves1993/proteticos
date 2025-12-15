package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Protetico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProteticoRepository extends JpaRepository<Protetico, Long> {

    // Método para buscar por email (herdado de Usuario)
    Optional<Protetico> findByEmail(String email);

    // Método para verificar se registro profissional existe
    boolean existsByRegistroProfissional(String registroProfissional);

    // Buscar por especialização
    List<Protetico> findByEspecializacaoContainingIgnoreCase(String especializacao);

    // Buscar por nome
    List<Protetico> findByNomeContaining(String nome);

    // Opcional: Buscar protéticos que aceitam terceirização
    List<Protetico> findByAceitaTerceirizacaoTrue();
}