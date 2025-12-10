package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.EtapaPedidoRepository;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EtapaService {

    @Autowired
    private EtapaPedidoRepository etapaRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    // Etapas padrão para cada tipo de serviço
    public void criarEtapasPadrao(Pedido pedido) {
        List<String> etapas = obterEtapasPorTipoServico(pedido.getTipoServico());

        for (int i = 0; i < etapas.size(); i++) {
            EtapaPedido etapa = new EtapaPedido();
            etapa.setPedido(pedido);
            etapa.setNomeEtapa(etapas.get(i));
            etapa.setOrdem(i + 1);
            etapa.setStatus(StatusEtapa.PENDENTE);
            etapa.setDataCriacao(LocalDateTime.now());

            // Definir responsável padrão para a primeira etapa
            if (i == 0) { // Primeira etapa - Recebimento
                etapa.setResponsavel(pedido.getProtetico());
                etapa.setStatus(StatusEtapa.EM_ANDAMENTO); // Primeira etapa começa automaticamente
                // Não temos dataInicio no modelo, então usamos dataPrevista como referência
                etapa.setDataPrevista(LocalDate.now().plusDays(1)); // Previsão para amanhã
            }

            etapaRepository.save(etapa);
        }

        System.out.println("✅ " + etapas.size() + " etapas criadas para pedido " + pedido.getCodigo());
    }

    private List<String> obterEtapasPorTipoServico(TipoServico tipoServico) {
        switch (tipoServico) {
            case COROA:
            case PONTE_FIXA:
            case ZIRCONIA:
                return Arrays.asList(
                        "📥 Recebimento",
                        "📷 Escaneamento",
                        "🗺️ Planejamento",
                        "⚙️ Usinagem",
                        "🔍 Prova",
                        "✨ Acabamento",
                        "🎨 Glaze",
                        "🚚 Entrega"
                );

            case PROVISORIO:
            case RESINA:
                return Arrays.asList(
                        "📥 Recebimento",
                        "🗺️ Planejamento",
                        "⚙️ Confecção",
                        "✨ Acabamento",
                        "🚚 Entrega"
                );

            case PROTESE_TOTAL:
            case PROTESE_PARCIAL:
                return Arrays.asList(
                        "📥 Recebimento",
                        "📷 Moldagem",
                        "🗺️ Planejamento",
                        "⚙️ Confecção Base",
                        "🔍 Prova Estrutural",
                        "🎨 Aplicação Dentes",
                        "✨ Acabamento",
                        "🚚 Entrega"
                );

            case IMPLANTE:
                return Arrays.asList(
                        "📥 Recebimento",
                        "📷 Escaneamento",
                        "🗺️ Planejamento Cirúrgico",
                        "⚙️ Prototipagem",
                        "🔍 Prova Protótipo",
                        "🎨 Confecção Definitiva",
                        "✨ Acabamento",
                        "🚚 Entrega"
                );

            default:
                return Arrays.asList(
                        "📥 Recebimento",
                        "🗺️ Planejamento",
                        "⚙️ Execução",
                        "🚚 Entrega"
                );
        }
    }

    public EtapaPedido concluirEtapa(Long etapaId, Long proteticoId) {
        EtapaPedido etapa = etapaRepository.findById(etapaId)
                .orElseThrow(() -> new RuntimeException("Etapa não encontrada"));

        // Verificar se o protético existe
        Protetico protetico = proteticoRepository.findById(proteticoId)
                .orElseThrow(() -> new RuntimeException("Protético não encontrado"));

        // Concluir a etapa atual usando o método do modelo
        etapa.concluir(); // Isso já seta status CONCLUIDA e dataConclusao
        etapa.setResponsavel(protetico);

        EtapaPedido etapaSalva = etapaRepository.save(etapa);

        // Iniciar automaticamente a próxima etapa
        iniciarProximaEtapa(etapa.getPedido().getId(), etapa.getOrdem(), proteticoId);

        return etapaSalva;
    }

    private void iniciarProximaEtapa(Long pedidoId, Integer ordemAtual, Long proteticoId) {
        // Buscar próxima etapa (ordem atual + 1)
        Optional<EtapaPedido> proximaEtapaOpt = etapaRepository
                .findByPedidoIdAndOrdem(pedidoId, ordemAtual + 1);

        if (proximaEtapaOpt.isPresent()) {
            EtapaPedido proximaEtapa = proximaEtapaOpt.get();
            Protetico protetico = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado"));

            // Iniciar a próxima etapa
            proximaEtapa.setStatus(StatusEtapa.EM_ANDAMENTO);
            proximaEtapa.setResponsavel(protetico);
            // Definir previsão para 2 dias a partir de hoje
            proximaEtapa.setDataPrevista(LocalDate.now().plusDays(2));

            etapaRepository.save(proximaEtapa);

            System.out.println("✅ Etapa " + proximaEtapa.getNomeEtapa() + " iniciada automaticamente");
        } else {
            System.out.println("🎉 Todas as etapas do pedido foram concluídas!");
        }
    }

    // Método para buscar etapas de um pedido
    public List<EtapaPedido> buscarEtapasPorPedido(Long pedidoId) {
        return etapaRepository.findByPedidoIdOrderByOrdemAsc(pedidoId);
    }

    // Método para buscar etapa atual (a primeira pendente ou em andamento)
    public EtapaPedido buscarEtapaAtual(Long pedidoId) {
        List<EtapaPedido> etapas = etapaRepository.findByPedidoIdOrderByOrdemAsc(pedidoId);

        return etapas.stream()
                .filter(e -> e.getStatus() == StatusEtapa.EM_ANDAMENTO || e.getStatus() == StatusEtapa.PENDENTE)
                .findFirst()
                .orElse(null);
    }
}