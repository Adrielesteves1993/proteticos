package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.DTO.*;
import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TerceirizacaoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Autowired
    private PedidoService pedidoService;

    /**
     * Solicita terceirização de um pedido
     */
    @Transactional
    public TerceirizacaoResponseDTO solicitarTerceirizacao(
            Long pedidoId,
            Long proteticoId,
            SolicitarTerceirizacaoRequest request) {

        // 1. Busca o pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        // 2. Valida se o protético solicitante é o dono do pedido
        if (!pedido.getProtetico().getId().equals(proteticoId)) {
            throw new RuntimeException("Apenas o protético responsável pelo pedido pode solicitar terceirização");
        }

        // 3. Valida se o pedido pode ser terceirizado
        if (!pedido.podeSerTerceirizado()) {
            throw new RuntimeException("Este pedido não pode ser terceirizado no momento. Status atual: " + pedido.getStatusPedido());
        }

        // 4. Busca o protético terceirizado
        Protetico proteticoTerceirizado = proteticoRepository.findById(request.getProteticoTerceirizadoId())
                .orElseThrow(() -> new RuntimeException("Protético terceirizado não encontrado"));

        // 5. Valida se o protético aceita terceirização
        if (!proteticoTerceirizado.isAceitaTerceirizacao()) {
            throw new RuntimeException("Este protético não aceita trabalhos terceirizados");
        }

        // 6. Valida percentual mínimo
        BigDecimal percentual = request.getPercentual();
        if (percentual == null || percentual.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Percentual deve ser maior que zero");
        }

        if (percentual.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new RuntimeException("Percentual não pode ser maior que 100%");
        }

        BigDecimal taxaMinima = proteticoTerceirizado.getTaxaMinimaTerceirizacao();
        if (taxaMinima != null && percentual.compareTo(taxaMinima) < 0) {
            throw new RuntimeException("Este protético aceita apenas terceirizações com percentual mínimo de " + taxaMinima + "%");
        }

        // 7. Valida especialidade (se o protético tiver especialidades definidas)
        if (!proteticoTerceirizado.aceitaTerceirizacaoPara(pedido.getTipoServico())) {
            throw new RuntimeException("Este protético não aceita terceirização para o tipo de serviço: " + pedido.getTipoServico());
        }

        // 8. Solicita a terceirização
        pedido.solicitarTerceirizacao(
                proteticoTerceirizado,
                percentual,
                request.getTipo(),
                request.getMotivo()
        );

        // 9. Salva as alterações
        Pedido pedidoAtualizado = pedidoRepository.save(pedido);

        // 10. Retorna DTO de resposta
        return new TerceirizacaoResponseDTO(pedidoAtualizado);
    }

    /**
     * Aceita uma terceirização (chamado pelo protético terceirizado)
     */
    @Transactional
    public TerceirizacaoResponseDTO aceitarTerceirizacao(Long pedidoId, Long proteticoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Valida se é o protético terceirizado
        if (pedido.getProteticoTerceirizado() == null ||
                !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
            throw new RuntimeException("Apenas o protético terceirizado pode aceitar esta solicitação");
        }

        // Valida status atual
        if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.SOLICITADO) {
            throw new RuntimeException("Esta terceirização não está aguardando aceitação. Status atual: " +
                    pedido.getStatusTerceirizacao());
        }

        // Aceita a terceirização
        pedido.aceitarTerceirizacao();

        // Atualiza status do pedido principal (opcional)
        // Aqui você pode decidir se muda o status do pedido ou não
        // Ex: pedido.setStatusPedido(StatusPedido.EM_TERCEIRIZACAO);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new TerceirizacaoResponseDTO(pedidoAtualizado);
    }

    /**
     * Recusa uma terceirização (chamado pelo protético terceirizado)
     */
    @Transactional
    public TerceirizacaoResponseDTO recusarTerceirizacao(Long pedidoId, Long proteticoId, String motivoRecusa) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Valida se é o protético terceirizado
        if (pedido.getProteticoTerceirizado() == null ||
                !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
            throw new RuntimeException("Apenas o protético terceirizado pode recusar esta solicitação");
        }

        // Valida status atual
        if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.SOLICITADO) {
            throw new RuntimeException("Esta terceirização não está aguardando resposta. Status atual: " +
                    pedido.getStatusTerceirizacao());
        }

        // Recusa a terceirização
        pedido.recusarTerceirizacao();

        // Adiciona motivo da recusa
        String novoMotivo = pedido.getMotivoTerceirizacao() +
                "\n\n--- RECUSADO ---\nMotivo: " + (motivoRecusa != null ? motivoRecusa : "Não informado");
        pedido.setMotivoTerceirizacao(novoMotivo);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new TerceirizacaoResponseDTO(pedidoAtualizado);
    }

    /**
     * Inicia a execução da terceirização (chamado pelo protético terceirizado)
     */
    @Transactional
    public TerceirizacaoResponseDTO iniciarTerceirizacao(Long pedidoId, Long proteticoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Valida se é o protético terceirizado
        if (pedido.getProteticoTerceirizado() == null ||
                !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
            throw new RuntimeException("Apenas o protético terceirizado pode iniciar a execução");
        }

        // Valida status atual
        if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.ACEITO) {
            throw new RuntimeException("A terceirização precisa estar ACEITA para iniciar. Status atual: " +
                    pedido.getStatusTerceirizacao());
        }

        // Inicia a terceirização
        pedido.iniciarTerceirizacao();

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new TerceirizacaoResponseDTO(pedidoAtualizado);
    }

    /**
     * Conclui a terceirização (chamado pelo protético terceirizado)
     */
    @Transactional
    public TerceirizacaoResponseDTO concluirTerceirizacao(Long pedidoId, Long proteticoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Valida se é o protético terceirizado
        if (pedido.getProteticoTerceirizado() == null ||
                !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
            throw new RuntimeException("Apenas o protético terceirizado pode concluir a execução");
        }

        // Valida status atual
        if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.EM_ANDAMENTO) {
            throw new RuntimeException("A terceirização precisa estar EM_ANDAMENTO para concluir. Status atual: " +
                    pedido.getStatusTerceirizacao());
        }

        // Conclui a terceirização
        pedido.concluirTerceirizacao();

        // Aqui você pode querer voltar o pedido para o protético titular
        // Ex: pedido.setStatusPedido(StatusPedido.EM_PRODUCAO);

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new TerceirizacaoResponseDTO(pedidoAtualizado);
    }

    /**
     * Cancela uma terceirização (pode ser chamado por qualquer um dos protéticos)
     */
    @Transactional
    public TerceirizacaoResponseDTO cancelarTerceirizacao(Long pedidoId, Long proteticoId, String motivo) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Valida se o protético tem permissão (titular ou terceirizado)
        boolean isTitular = pedido.getProtetico().getId().equals(proteticoId);
        boolean isTerceirizado = pedido.getProteticoTerceirizado() != null &&
                pedido.getProteticoTerceirizado().getId().equals(proteticoId);

        if (!isTitular && !isTerceirizado) {
            throw new RuntimeException("Apenas os protéticos envolvidos na terceirização podem cancelar");
        }

        // Valida se pode cancelar
        if (!pedido.getStatusTerceirizacao().isAtivo()) {
            throw new RuntimeException("Esta terceirização já está finalizada. Status atual: " +
                    pedido.getStatusTerceirizacao());
        }

        // Cancela a terceirização
        pedido.cancelarTerceirizacao();

        // Adiciona motivo do cancelamento
        if (motivo != null && !motivo.trim().isEmpty()) {
            String novoMotivo = pedido.getMotivoTerceirizacao() +
                    "\n\n--- CANCELADO ---\nMotivo: " + motivo +
                    "\nCancelado por: " + (isTitular ? "Protético Titular" : "Protético Terceirizado");
            pedido.setMotivoTerceirizacao(novoMotivo);
        }

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return new TerceirizacaoResponseDTO(pedidoAtualizado);
    }

    /**
     * Lista todas terceirizações de um protético (como solicitante ou executor)
     */
    public List<TerceirizacaoResponseDTO> listarTerceirizacoesPorProtetico(Long proteticoId) {
        // Busca pedidos onde o protético é o titular (solicitante)
        List<Pedido> comoSolicitante = pedidoRepository.findByProteticoId(proteticoId)
                .stream()
                .filter(Pedido::isTerceirizado)
                .collect(Collectors.toList());

        // Busca pedidos onde o protético é o terceirizado (executor)
        // Nota: Você pode precisar criar um método no repository para isso
        List<Pedido> comoExecutor = pedidoRepository.findAll()
                .stream()
                .filter(p -> p.getProteticoTerceirizado() != null &&
                        p.getProteticoTerceirizado().getId().equals(proteticoId))
                .collect(Collectors.toList());

        // Combina as listas
        List<Pedido> todas = new ArrayList<>();
        todas.addAll(comoSolicitante);
        todas.addAll(comoExecutor);

        // Converte para DTO
        return todas.stream()
                .map(TerceirizacaoResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca terceirização por ID do pedido
     */
    public TerceirizacaoResponseDTO buscarPorPedidoId(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (!pedido.isTerceirizado()) {
            throw new RuntimeException("Este pedido não está terceirizado");
        }

        return new TerceirizacaoResponseDTO(pedido);
    }

    /**
     * Lista protéticos disponíveis para terceirização
     */
    public List<ProteticoSimplesDTO> listarProteticosDisponiveis(Long pedidoId, TipoServico tipoServico) {
        // Busca o pedido para validar
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // Busca protéticos que aceitam terceirização para este tipo de serviço
        List<Protetico> proteticos = proteticoRepository.findAll()
                .stream()
                .filter(p -> p.isAceitaTerceirizacao())
                .filter(p -> p.aceitaTerceirizacaoPara(tipoServico != null ? tipoServico : pedido.getTipoServico()))
                .filter(p -> !p.getId().equals(pedido.getProtetico().getId())) // Remove o protético atual
                .collect(Collectors.toList());

        // Converte para DTO
        return proteticos.stream()
                .map(p -> {
                    ProteticoSimplesDTO dto = new ProteticoSimplesDTO();
                    dto.setId(p.getId());
                    dto.setNome(p.getNome());
                    dto.setEmail(p.getEmail());
                    dto.setRegistroProfissional(p.getRegistroProfissional());
                    dto.setEspecializacao(p.getEspecializacao());
                    dto.setNotaTerceirizacao(p.getNotaTerceirizacao());
                    dto.setQuantidadeTerceirizacoes(p.getQuantidadeTerceirizacoes());
                    dto.setTaxaMinimaTerceirizacao(p.getTaxaMinimaTerceirizacao());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}