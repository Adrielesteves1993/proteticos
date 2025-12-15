package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.DTO.*;
import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Autowired
    private EtapaPedidoRepository etapaPedidoRepository;

    // ============ MÉTODOS DE CONVERSÃO PARA DTO ============

    public PedidoResponseDTO converterParaDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        PedidoResponseDTO dto = new PedidoResponseDTO();
        dto.setId(pedido.getId());
        dto.setCodigo(pedido.getCodigo());
        dto.setTipoServico(pedido.getTipoServico());
        dto.setInformacoesDetalhadas(pedido.getInformacoesDetalhadas());
        dto.setValorCobrado(pedido.getValorCobrado());
        dto.setDataEntrada(pedido.getDataEntrada());
        dto.setDataPrevistaEntrega(pedido.getDataPrevistaEntrega());
        dto.setDataEntrega(pedido.getDataEntrega());
        dto.setStatus(pedido.getStatus());
        dto.setDataCriacao(pedido.getDataCriacao());

        // Converter dentista
        if (pedido.getDentista() != null) {
            DentistaSimplesDTO dentistaDTO = new DentistaSimplesDTO();
            dentistaDTO.setId(pedido.getDentista().getId());
            dentistaDTO.setNome(pedido.getDentista().getNome());
            dentistaDTO.setEmail(pedido.getDentista().getEmail());
            dentistaDTO.setCro(pedido.getDentista().getCro());
            dentistaDTO.setEspecialidade(pedido.getDentista().getEspecialidade()); // ← ADICIONE
            dto.setDentista(dentistaDTO);
        }

        // Converter protético - CORRIJA AQUI!
        if (pedido.getProtetico() != null) {
            ProteticoSimplesDTO proteticoDTO = new ProteticoSimplesDTO();
            proteticoDTO.setId(pedido.getProtetico().getId());
            proteticoDTO.setNome(pedido.getProtetico().getNome());
            proteticoDTO.setEmail(pedido.getProtetico().getEmail());

            // ⚠️ ESTES CAMPOS ESTÃO FALTANDO!
            proteticoDTO.setRegistroProfissional(pedido.getProtetico().getRegistroProfissional());
            proteticoDTO.setEspecializacao(pedido.getProtetico().getEspecializacao());
            proteticoDTO.setAceitaTerceirizacao(pedido.getProtetico().isAceitaTerceirizacao());

            dto.setProtetico(proteticoDTO);
        }

        return dto;
    }

    // MÉTODO FALTANTE - ADICIONE ESTE
    public List<PedidoResponseDTO> converterListaParaDTO(List<Pedido> pedidos) {
        if (pedidos == null || pedidos.isEmpty()) {
            return new ArrayList<>();
        }

        return pedidos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    // ============ MÉTODOS DE CRIAÇÃO ============

    @Transactional
    public Pedido criarPedido(CriarPedidoRequest request) {
        // Buscar dentista e protético
        Dentista dentista = dentistaRepository.findById(request.getDentistaId())
                .orElseThrow(() -> new RuntimeException("Dentista não encontrado com ID: " + request.getDentistaId()));

        Protetico protetico = proteticoRepository.findById(request.getProteticoId())
                .orElseThrow(() -> new RuntimeException("Protético não encontrado com ID: " + request.getProteticoId()));

        // Criar pedido
        Pedido pedido = new Pedido();
        pedido.setDentista(dentista);
        pedido.setProtetico(protetico);
        pedido.setTipoServico(request.getTipoServico());
        pedido.setInformacoesDetalhadas(request.getInformacoesDetalhadas());
        pedido.setValorCobrado(request.getValorCobrado());
        pedido.setDataEntrada(LocalDate.now());
        pedido.setDataPrevistaEntrega(request.getDataPrevistaEntrega());
        pedido.setStatus(StatusPedido.AGUARDANDO_APROVACAO);
        pedido.setDataCriacao(LocalDateTime.now());

        // Salvar pedido
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Criar etapas iniciais se necessário
        if (request.isCriarEtapasIniciais()) {
            criarEtapasIniciais(pedidoSalvo);
        }

        return pedidoSalvo;
    }

    // ============ MÉTODOS DE STATUS (FLUXO CONTROLADO) ============

    @Transactional
    public Pedido aprovarPedido(Long pedidoId) {
        return mudarStatus(pedidoId, StatusPedido.APROVADO);
    }

    @Transactional
    public Pedido iniciarProducao(Long pedidoId) {
        return mudarStatus(pedidoId, StatusPedido.EM_PRODUCAO);
    }

    @Transactional
    public Pedido finalizarPedido(Long pedidoId) {
        Pedido pedido = mudarStatus(pedidoId, StatusPedido.FINALIZADO);

        // Define data de conclusão
        pedido.setDataEntrega(LocalDate.now());
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cancelarPedido(Long pedidoId) {
        Pedido pedido = mudarStatus(pedidoId, StatusPedido.CANCELADO);

        // Define data de cancelamento
        pedido.setDataCancelamento(LocalDate.now());
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    // ============ MÉTODO PRIVADO PARA MUDANÇA DE STATUS ============

    @Transactional
    private Pedido mudarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        // Valida se pode fazer a transição
        if (!pedido.getStatus().podeMudarPara(novoStatus)) {
            throw new RuntimeException(
                    "Não é possível mudar de " + pedido.getStatus() + " para " + novoStatus +
                            ". Transição não permitida."
            );
        }

        System.out.println("✅ Mudando pedido " + pedidoId +
                " de " + pedido.getStatus() + " para " + novoStatus);

        pedido.setStatus(novoStatus);
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    // ============ MÉTODO GENÉRICO PARA ATUALIZAÇÃO DE STATUS ============

    @Transactional
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        return mudarStatus(pedidoId, novoStatus);
    }

    // ============ MÉTODOS AUXILIARES ============

    public List<StatusPedido> getProximosStatusPossiveis(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        // Usa o método getProximosStatus() que você adicionou no enum
        return Arrays.asList(pedido.getStatus().getProximosStatus());
    }

    // ============ OUTROS MÉTODOS ============

    private void criarEtapasIniciais(Pedido pedido) {
        // Usando o método factory (opção 1)
        EtapaPedido etapa1 = EtapaPedido.criarEtapaInicial(
                pedido, "Recebimento", "Pedido recebido do dentista", 1
        );
        etapaPedidoRepository.save(etapa1); // @PrePersist será chamado aqui!

        // Ou usando construtor normal (opção 2)
        EtapaPedido etapa2 = new EtapaPedido();
        etapa2.setPedido(pedido);
        etapa2.setNomeEtapa("Planejamento");
        etapa2.setObservacoes("Planejamento do trabalho protético");
        etapa2.setStatus(StatusEtapa.PENDENTE);
        etapa2.setOrdem(2);
        // NÃO precisa mais: etapa2.setDataCriacao(LocalDateTime.now());
        etapaPedidoRepository.save(etapa2); // @PrePersist será chamado aqui!
    }

    public List<Pedido> listarTodosPedidos() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> listarPedidosPorProtetico(Long proteticoId) {
        return pedidoRepository.findByProteticoId(proteticoId);
    }

    public List<Pedido> listarPedidosPorDentista(Long dentistaId) {
        return pedidoRepository.findByDentistaId(dentistaId);
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido buscarPorCodigo(String codigo) {
        return pedidoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com código: " + codigo));
    }

    @Transactional
    public Pedido atualizarValor(Long pedidoId, BigDecimal novoValor) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        // Só permite editar se não estiver finalizado
        if (pedido.getStatus().isEstadoFinal()) {
            throw new RuntimeException("Não é possível editar um pedido finalizado ou cancelado");
        }

        pedido.setValorCobrado(novoValor);
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarDataPrevista(Long pedidoId, LocalDate novaDataPrevista) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        // Só permite editar se não estiver finalizado
        if (pedido.getStatus().isEstadoFinal()) {
            throw new RuntimeException("Não é possível editar um pedido finalizado ou cancelado");
        }

        pedido.setDataPrevistaEntrega(novaDataPrevista);
        pedido.setDataUltimaAtualizacao(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void excluirPedido(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new RuntimeException("Pedido não encontrado com ID: " + pedidoId);
        }
        pedidoRepository.deleteById(pedidoId);
    }

    // Mudar findByStatus() para findByStatusPedido()
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatusPedido(status); // ← CORREÇÃO
    }

    public List<Pedido> buscarAtrasados() {
        LocalDate hoje = LocalDate.now();
        return pedidoRepository.findByDataPrevistaEntregaBeforeAndStatusPedidoNot(
                hoje,
                StatusPedido.FINALIZADO
        );
    }
}