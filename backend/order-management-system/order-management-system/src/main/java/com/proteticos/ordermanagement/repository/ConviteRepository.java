// repository/ConviteRepository.java
package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Convite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConviteRepository extends JpaRepository<Convite, Long> {
    Optional<Convite> findByCodigo(String codigo);
    Optional<Convite> findByCodigoAndUtilizadoFalse(String codigo);
    boolean existsByCodigo(String codigo);
    long countByCriadoPorIdAndUtilizadoFalse(Long usuarioId);

    // ✅ ADICIONE ESTE MÉTODO - para o endpoint listarPorCriador()
    List<Convite> findByCriadoPorId(Long usuarioId);
}