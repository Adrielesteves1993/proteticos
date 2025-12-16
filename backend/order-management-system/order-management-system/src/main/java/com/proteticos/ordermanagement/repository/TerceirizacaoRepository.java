package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Terceirizacao;
import com.proteticos.ordermanagement.model.StatusTerceirizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TerceirizacaoRepository extends JpaRepository<Terceirizacao, Long> {

    // Métodos que retornam List
    List<Terceirizacao> findByProteticoDestinoIdAndStatus(Long proteticoDestinoId, StatusTerceirizacao status);
    List<Terceirizacao> findByProteticoOrigemId(Long proteticoOrigemId);
    List<Terceirizacao> findByProteticoDestinoId(Long proteticoDestinoId);
    List<Terceirizacao> findByPedidoId(Long pedidoId);
    List<Terceirizacao> findByPedidoIdIn(List<Long> pedidoIds);
    List<Terceirizacao> findByStatus(StatusTerceirizacao status);
    List<Terceirizacao> findByPedidoIdAndStatus(Long pedidoId, StatusTerceirizacao status);
    List<Terceirizacao> findByStatusIn(List<StatusTerceirizacao> statuses);

    // Métodos que retornam Optional (CRITICAIS - ADICIONE ESTES)
    Optional<Terceirizacao> findByPedidoIdAndProteticoDestinoId(Long pedidoId, Long proteticoDestinoId);

    // Adicione este método para resolver o erro da linha 390
    @Query("SELECT t FROM Terceirizacao t WHERE t.pedido.id = :pedidoId ORDER BY t.id DESC LIMIT 1")
    Optional<Terceirizacao> findTopByPedidoId(Long pedidoId);

    // Ou se preferir sem @Query:
    Optional<Terceirizacao> findFirstByPedidoIdOrderByIdDesc(Long pedidoId);
}