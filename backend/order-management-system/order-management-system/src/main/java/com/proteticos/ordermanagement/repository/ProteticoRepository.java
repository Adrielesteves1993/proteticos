// src/main/java/com/proteticos/ordermanagement/repository/ProteticoRepository.java
package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Protetico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProteticoRepository extends JpaRepository<Protetico, Long> {

    Optional<Protetico> findByEmail(String email);

    List<Protetico> findByAtivoTrue();

    List<Protetico> findByEspecializacaoContainingIgnoreCase(String especializacao);

    List<Protetico> findByAceitaTerceirizacaoTrue();

    @Query("SELECT p FROM Protetico p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Protetico> findByNomeContaining(@Param("nome") String nome);

    boolean existsByRegistroProfissional(String registroProfissional);
}