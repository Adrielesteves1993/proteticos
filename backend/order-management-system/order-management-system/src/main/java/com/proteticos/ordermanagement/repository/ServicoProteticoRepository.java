package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.ServicoProtetico;
import com.proteticos.ordermanagement.model.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoProteticoRepository extends JpaRepository<ServicoProtetico, Long> {

    Optional<ServicoProtetico> findByProteticoIdAndTipoServico(Long proteticoId, TipoServico tipoServico);

    List<ServicoProtetico> findByProteticoId(Long proteticoId);

    List<ServicoProtetico> findByProteticoIdAndAtivoTrue(Long proteticoId);

    List<ServicoProtetico> findByTipoServicoAndAtivoTrue(TipoServico tipoServico);

    boolean existsByProteticoIdAndTipoServico(Long proteticoId, TipoServico tipoServico);

    @Query("SELECT COUNT(sp) > 0 FROM ServicoProtetico sp WHERE sp.protetico.id = :proteticoId AND sp.tipoServico = :tipoServico AND sp.ativo = true")
    boolean existsServicoAtivo(@Param("proteticoId") Long proteticoId, @Param("tipoServico") TipoServico tipoServico);
}