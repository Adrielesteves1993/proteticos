package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Terceirizacao;
import com.proteticos.ordermanagement.model.StatusTerceirizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TerceirizacaoRepository extends JpaRepository<Terceirizacao, Long> {

    // Solicitações recebidas por um protético (onde ele é o destino)
    List<Terceirizacao> findByProteticoDestinoIdAndStatus(Long proteticoDestinoId, StatusTerceirizacao status);

    // Solicitações enviadas por um protético (onde ele é a origem)
    List<Terceirizacao> findByProteticoOrigemId(Long proteticoOrigemId);

    // Todas as solicitações recebidas por um protético
    List<Terceirizacao> findByProteticoDestinoId(Long proteticoDestinoId);

    // Terceirizações de um pedido específico
    List<Terceirizacao> findByPedidoId(Long pedidoId);

    // Verificar se já existe solicitação para evitar duplicatas
    Optional<Terceirizacao> findByPedidoIdAndProteticoDestinoId(Long pedidoId, Long proteticoDestinoId);
}