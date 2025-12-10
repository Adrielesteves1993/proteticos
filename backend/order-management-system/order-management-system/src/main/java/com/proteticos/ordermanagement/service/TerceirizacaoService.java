package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.*;
import com.proteticos.ordermanagement.DTO.SolicitacaoTerceirizacaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TerceirizacaoService {

    @Autowired
    private TerceirizacaoRepository terceirizacaoRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    public Terceirizacao solicitarTerceirizacao(SolicitacaoTerceirizacaoDTO solicitacaoDTO) {
        // Validar protético origem
        Optional<Protetico> proteticoOrigem = proteticoRepository.findById(solicitacaoDTO.getProteticoOrigemId());
        if (proteticoOrigem.isEmpty()) {
            throw new RuntimeException("Protético origem não encontrado");
        }

        // Validar protético destino
        Optional<Protetico> proteticoDestino = proteticoRepository.findById(solicitacaoDTO.getProteticoDestinoId());
        if (proteticoDestino.isEmpty()) {
            throw new RuntimeException("Protético destino não encontrado");
        }

        // Validar pedido
        Optional<Pedido> pedido = pedidoRepository.findById(solicitacaoDTO.getPedidoId());
        if (pedido.isEmpty()) {
            throw new RuntimeException("Pedido não encontrado");
        }

        // Verificar se pedido pertence ao protético origem
        if (!pedido.get().getProtetico().getId().equals(solicitacaoDTO.getProteticoOrigemId())) {
            throw new RuntimeException("Pedido não pertence ao protético origem");
        }

        // Verificar se já existe solicitação
        Optional<Terceirizacao> terceirizacaoExistente = terceirizacaoRepository
                .findByPedidoIdAndProteticoDestinoId(solicitacaoDTO.getPedidoId(), solicitacaoDTO.getProteticoDestinoId());

        if (terceirizacaoExistente.isPresent()) {
            throw new RuntimeException("Já existe uma solicitação de terceirização para este pedido e protético destino");
        }

        // Criar terceirização
        Terceirizacao terceirizacao = new Terceirizacao(
                proteticoOrigem.get(),
                proteticoDestino.get(),
                pedido.get(),
                solicitacaoDTO.getServicoSolicitado()
        );

        terceirizacao.setObservacoes(solicitacaoDTO.getObservacoes());
        terceirizacao.setValorCombinado(solicitacaoDTO.getValorCombinado());

        return terceirizacaoRepository.save(terceirizacao);
    }

    public Terceirizacao aceitarSolicitacao(Long terceirizacaoId, Long proteticoDestinoId) {
        Optional<Terceirizacao> terceirizacaoOpt = terceirizacaoRepository.findById(terceirizacaoId);

        if (terceirizacaoOpt.isEmpty()) {
            throw new RuntimeException("Terceirização não encontrada");
        }

        Terceirizacao terceirizacao = terceirizacaoOpt.get();

        // Validar se o protético destino é o correto
        if (!terceirizacao.getProteticoDestino().getId().equals(proteticoDestinoId)) {
            throw new RuntimeException("Esta solicitação não pertence a este protético");
        }

        // Validar se está no status correto
        if (terceirizacao.getStatus() != StatusTerceirizacao.SOLICITADA) {
            throw new RuntimeException("Solicitação não pode ser aceita no status atual: " + terceirizacao.getStatus());
        }

        terceirizacao.setStatus(StatusTerceirizacao.ACEITA);
        terceirizacao.setAceitoEm(LocalDateTime.now());

        return terceirizacaoRepository.save(terceirizacao);
    }

    public Terceirizacao recusarSolicitacao(Long terceirizacaoId, Long proteticoDestinoId) {
        Optional<Terceirizacao> terceirizacaoOpt = terceirizacaoRepository.findById(terceirizacaoId);

        if (terceirizacaoOpt.isEmpty()) {
            throw new RuntimeException("Terceirização não encontrada");
        }

        Terceirizacao terceirizacao = terceirizacaoOpt.get();

        // Validar se o protético destino é o correto
        if (!terceirizacao.getProteticoDestino().getId().equals(proteticoDestinoId)) {
            throw new RuntimeException("Esta solicitação não pertence a este protético");
        }

        terceirizacao.setStatus(StatusTerceirizacao.RECUSADA);

        return terceirizacaoRepository.save(terceirizacao);
    }

    public List<Terceirizacao> getSolicitacoesRecebidas(Long proteticoDestinoId) {
        return terceirizacaoRepository.findByProteticoDestinoIdAndStatus(
                proteticoDestinoId, StatusTerceirizacao.SOLICITADA);
    }

    public List<Terceirizacao> getSolicitacoesEnviadas(Long proteticoOrigemId) {
        return terceirizacaoRepository.findByProteticoOrigemId(proteticoOrigemId);
    }
    // ... (seus métodos existentes já estão aqui)

    public List<Terceirizacao> getTerceirizacoesPorPedido(Long pedidoId) {
        return terceirizacaoRepository.findByPedidoId(pedidoId);
    }

} // ← Este é o } que fecha a classe
