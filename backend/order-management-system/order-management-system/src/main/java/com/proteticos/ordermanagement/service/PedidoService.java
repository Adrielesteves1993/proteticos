package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.dto.*;
import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    // ============ NOVO MÉTODO: Converter Pedido para DTO ============
// NO PedidoService.java - ATUALIZE O MÉTODO converterParaDTO
// NO PedidoService.java - ATUALIZE A PARTE DO PROTÉTICO NO MÉTODO converterParaDTO
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
            dentistaDTO.setEspecialidade(pedido.getDentista().getEspecialidade());
            dto.setDentista(dentistaDTO);
        }

        // Converter protético - COM OS CAMPOS CORRETOS
        if (pedido.getProtetico() != null) {
            ProteticoSimplesDTO proteticoDTO = new ProteticoSimplesDTO();
            proteticoDTO.setId(pedido.getProtetico().getId());
            proteticoDTO.setNome(pedido.getProtetico().getNome());
            proteticoDTO.setEmail(pedido.getProtetico().getEmail());
            proteticoDTO.setRegistroProfissional(pedido.getProtetico().getRegistroProfissional());
            proteticoDTO.setEspecializacao(pedido.getProtetico().getEspecializacao());
            proteticoDTO.setAceitaTerceirizacao(pedido.getProtetico().isAceitaTerceirizacao());
            proteticoDTO.setValorHora(pedido.getProtetico().getValorHora());
            proteticoDTO.setCapacidadePedidosSimultaneos(pedido.getProtetico().getCapacidadePedidosSimultaneos());
            dto.setProtetico(proteticoDTO);
        }

        return dto;
    }
    public List<PedidoResponseDTO> converterListaParaDTO(List<Pedido> pedidos) {
        return pedidos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    // ============ FIM DO NOVO MÉTODO ============

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

        // Datas
        if (request.getDataEntrada() != null) {
            pedido.setDataEntrada(request.getDataEntrada());
        } else {
            pedido.setDataEntrada(LocalDate.now());
        }

        pedido.setDataPrevistaEntrega(request.getDataPrevistaEntrega());

        // Status inicial: AGUARDANDO_APROVACAO (quando dentista cria)
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

    @Transactional
    public Pedido aprovarPedido(Long pedidoId) {
        try {
            System.out.println("=== APROVANDO PEDIDO NO SERVICE ===");
            System.out.println("Pedido ID: " + pedidoId);

            // Busca o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

            System.out.println("Status atual: " + pedido.getStatus());
            System.out.println("Código: " + pedido.getCodigo());

            // Verifica se pode ser aprovado
            if (pedido.getStatus() != StatusPedido.AGUARDANDO_APROVACAO) {
                System.err.println("❌ Pedido não está aguardando aprovação. Status: " + pedido.getStatus());
                throw new RuntimeException("Pedido não está aguardando aprovação. Status atual: " + pedido.getStatus());
            }

            // Muda para APROVADO
            pedido.setStatus(StatusPedido.APROVADO);
            System.out.println("✅ Mudando status para: APROVADO");

            // Salva as alterações
            Pedido pedidoAprovado = pedidoRepository.save(pedido);

            System.out.println("✅ Pedido aprovado com sucesso! Novo status: " + pedidoAprovado.getStatus());
            return pedidoAprovado;

        } catch (Exception e) {
            System.err.println("💥 Erro no service ao aprovar pedido: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void criarEtapasIniciais(Pedido pedido) {
        // Etapa 1: Recebimento
        EtapaPedido etapa1 = new EtapaPedido();
        etapa1.setPedido(pedido);
        etapa1.setNomeEtapa("Recebimento");
        etapa1.setObservacoes("Pedido recebido do dentista");
        etapa1.setStatus(StatusEtapa.PENDENTE);
        etapa1.setOrdem(1);
        etapaPedidoRepository.save(etapa1);

        // Etapa 2: Planejamento
        EtapaPedido etapa2 = new EtapaPedido();
        etapa2.setPedido(pedido);
        etapa2.setNomeEtapa("Planejamento");
        etapa2.setObservacoes("Planejamento do trabalho protético");
        etapa2.setStatus(StatusEtapa.PENDENTE);
        etapa2.setOrdem(2);
        etapaPedidoRepository.save(etapa2);
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
    public Pedido atualizarStatus(Long pedidoId, StatusPedido novoStatus) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        System.out.println("Atualizando pedido " + pedidoId + " de " + pedido.getStatus() + " para " + novoStatus);

        pedido.setStatus(novoStatus);

        // Se for marcado como ENTREGUE, definir data de entrega
        if (novoStatus == StatusPedido.ENTREGUE && pedido.getDataEntrega() == null) {
            pedido.setDataEntrega(LocalDate.now());
        }

        // Se for FINALIZADO/CONCLUIDO e não tiver data de entrega
        if ((novoStatus == StatusPedido.FINALIZADO || novoStatus == StatusPedido.CONCLUIDO)
                && pedido.getDataEntrega() == null) {
            pedido.setDataEntrega(LocalDate.now());
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarValor(Long pedidoId, BigDecimal novoValor) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        pedido.setValorCobrado(novoValor);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizarDataPrevista(Long pedidoId, LocalDate novaDataPrevista) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        pedido.setDataPrevistaEntrega(novaDataPrevista);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void excluirPedido(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new RuntimeException("Pedido não encontrado com ID: " + pedidoId);
        }
        pedidoRepository.deleteById(pedidoId);
    }

    @Transactional
    public Pedido finalizarPedido(Long pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com ID: " + pedidoId));

        pedido.setStatus(StatusPedido.CONCLUIDO);
        pedido.setDataEntrega(LocalDate.now());

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> buscarPorStatus(StatusPedido status) {
        return pedidoRepository.findByStatus(status);
    }

    public List<Pedido> buscarAtrasados() {
        LocalDate hoje = LocalDate.now();
        return pedidoRepository.findByDataPrevistaEntregaBeforeAndStatusNot(hoje, StatusPedido.ENTREGUE);
    }
}