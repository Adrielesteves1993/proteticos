package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.ServicoProtetico;
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.model.PoliticaExecucaoServico; // NOVO IMPORT AQUI
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicoProteticoRepository extends JpaRepository<ServicoProtetico, Long> {

    // Métodos existentes
    Optional<ServicoProtetico> findByProteticoIdAndTipoServico(Long proteticoId, TipoServico tipoServico);
    List<ServicoProtetico> findByProteticoId(Long proteticoId);
    List<ServicoProtetico> findByProteticoIdAndAtivoTrue(Long proteticoId);
    List<ServicoProtetico> findByTipoServicoAndAtivoTrue(TipoServico tipoServico);
    boolean existsByProteticoIdAndTipoServico(Long proteticoId, TipoServico tipoServico);

    @Query("SELECT COUNT(sp) > 0 FROM ServicoProtetico sp WHERE sp.protetico.id = :proteticoId AND sp.tipoServico = :tipoServico AND sp.ativo = true")
    boolean existsServicoAtivo(@Param("proteticoId") Long proteticoId, @Param("tipoServico") TipoServico tipoServico);

    // NOVO: Busca serviços por política de execução (para terceirização)
    @Query("SELECT s FROM ServicoProtetico s WHERE s.tipoServico = :tipoServico AND s.politicaExecucao IN :politicas AND s.ativo = true")
    List<ServicoProtetico> findByTipoServicoAndPoliticaExecucaoInAndAtivoTrue(
            @Param("tipoServico") TipoServico tipoServico,
            @Param("politicas") List<PoliticaExecucaoServico> politicas
    );

    // NOVO: Busca serviços que um protético pode terceirizar
    @Query("SELECT s FROM ServicoProtetico s WHERE s.protetico.id = :proteticoId AND s.politicaExecucao IN :politicas")
    List<ServicoProtetico> findByProteticoIdAndPoliticaExecucaoIn(
            @Param("proteticoId") Long proteticoId,
            @Param("politicas") List<PoliticaExecucaoServico> politicas
    );

    // NOVO: Busca terceirizado preferido por ID
    @Query("SELECT s FROM ServicoProtetico s WHERE s.terceirizadoPreferidoId = :terceirizadoId AND s.ativo = true")
    List<ServicoProtetico> findByTerceirizadoPreferidoId(@Param("terceirizadoId") Long terceirizadoId);
}