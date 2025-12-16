package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.DTO.*;
import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private TerceirizacaoRepository terceirizacaoRepository;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    @Lazy
    private ServicoProteticoService servicoProteticoService;

    @Autowired
    private ServicoProteticoRepository servicoProteticoRepository;

    // ============ MÉTODOS QUE FALTAM ============

    /**
     * MÉTODO ORIGINAL: Lista protéticos disponíveis para terceirização
     * Aceita TipoServico enum (necessário para compatibilidade)
     */
    public List<ProteticoSimplesDTO> listarProteticosDisponiveis(Long pedidoId, TipoServico tipoServico) {
        System.out.println("🔄 listarProteticosDisponiveis (com enum) chamado");
        System.out.println("📝 pedidoId: " + pedidoId + ", tipoServico: " + tipoServico);

        // Se tipoServico for null, tenta buscar do pedido
        TipoServico tipoParaBusca = tipoServico;

        if (tipoParaBusca == null && pedidoId != null) {
            try {
                Pedido pedido = pedidoRepository.findById(pedidoId)
                        .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));
                tipoParaBusca = pedido.getTipoServico();
                System.out.println("✅ Tipo obtido do pedido: " + tipoParaBusca);
            } catch (Exception e) {
                System.err.println("⚠️ Erro ao obter pedido: " + e.getMessage());
                throw new RuntimeException("Não foi possível obter tipoServico do pedido", e);
            }
        }

        if (tipoParaBusca == null) {
            System.err.println("❌ Tipo de serviço não especificado");
            throw new RuntimeException("Tipo de serviço não especificado");
        }

        // Buscar o pedido para obter o protético atual (se existir)
        final Long proteticoAtualId = (pedidoId != null)
                ? pedidoRepository.findById(pedidoId)
                .map(Pedido::getProtetico)
                .map(Protetico::getId)
                .orElse(null)
                : null;

        System.out.println("🔍 Protético atual do pedido ID: " + proteticoAtualId);

        // Cria uma cópia final para usar na lambda
        final TipoServico tipoFinal = tipoParaBusca;

        // Busca protéticos que aceitam terceirização para este tipo de serviço
        // ATUALIZADO: Verifica pelos serviços do protético, não mais pelo campo aceitaTerceirizacao
        List<Protetico> proteticos = proteticoRepository.findAll()
                .stream()
                // NOVA LÓGICA: Filtra protéticos que têm serviço ativo que permite terceirização
                .filter(p -> {
                    // Verifica se tem serviços que permitem terceirização para este tipo
                    return p.getServicosProtetico().stream()
                            .anyMatch(servico ->
                                    servico.isAtivo() &&
                                            servico.getTipoServico() == tipoFinal &&
                                            servico.getPoliticaExecucao() != null &&
                                            (servico.getPoliticaExecucao() == PoliticaExecucaoServico.TERCEIRIZADO ||
                                                    servico.getPoliticaExecucao() == PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO)  // ← CORREÇÃO
                            );
                })
                .filter(p -> {
                    // Usa a variável final dentro do lambda
                    if (proteticoAtualId == null) {
                        return true;
                    }
                    return !p.getId().equals(proteticoAtualId);
                })
                .collect(Collectors.toList());

        System.out.println("✅ " + proteticos.size() + " protéticos encontrados");

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

    /**
     * MÉTODO ALTERNATIVO: Lista protéticos por tipo de serviço (enum)
     * Versão sem pedidoId
     */
    public List<ProteticoSimplesDTO> listarProteticosPorTipoServico(TipoServico tipoServico) {
        System.out.println("🔄 listarProteticosPorTipoServico (enum)");
        System.out.println("📝 tipoServico: " + tipoServico);

        if (tipoServico == null) {
            throw new RuntimeException("Tipo de serviço não pode ser null");
        }

        // Cria uma cópia final para usar na lambda
        final TipoServico tipoFinal = tipoServico;

        // Busca protéticos que aceitam terceirização para este tipo de serviço
        // ATUALIZADO: Verifica pelos serviços do protético
        List<Protetico> proteticos = proteticoRepository.findAll()
                .stream()
                // NOVA LÓGICA: Filtra protéticos que têm serviço ativo que permite terceirização
                .filter(p -> {
                    return p.getServicosProtetico().stream()
                            .anyMatch(servico ->
                                    servico.isAtivo() &&
                                            servico.getTipoServico() == tipoFinal &&
                                            servico.getPoliticaExecucao() != null &&
                                            (servico.getPoliticaExecucao() == PoliticaExecucaoServico.TERCEIRIZADO ||
                                                    servico.getPoliticaExecucao() == PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO)  // ← CORREÇÃO
                            );
                })
                .collect(Collectors.toList());

        System.out.println("✅ " + proteticos.size() + " protéticos encontrados");

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

    /**
     * Solicita terceirização de um pedido
     * @param pedidoId ID do pedido
     * @param proteticoId ID do protético que ESTÁ solicitando (dono do pedido)
     * @param request DTO com informações da terceirização
     */
    public TerceirizacaoResponseDTO solicitarTerceirizacao(
            Long pedidoId,
            Long proteticoId,
            SolicitarTerceirizacaoRequest request) {

        System.out.println("🔄 solicitarTerceirizacao (3 parâmetros)");
        System.out.println("📝 pedidoId: " + pedidoId);
        System.out.println("📝 proteticoId (solicitante): " + proteticoId);
        System.out.println("📝 request: " + request);

        try {
            // 1. Validações básicas
            if (request == null) {
                throw new RuntimeException("Request não pode ser nulo");
            }

            if (request.getProteticoTerceirizadoId() == null) {
                throw new RuntimeException("ID do protético terceirizado é obrigatório");
            }

            // 2. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 3. Buscar o protético solicitante
            Protetico proteticoSolicitante = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético solicitante não encontrado: " + proteticoId));

            // 4. Verificar se é o dono do pedido
            if (pedido.getProtetico() == null) {
                throw new RuntimeException("Pedido não tem protético responsável");
            }

            if (!pedido.getProtetico().getId().equals(proteticoId)) {
                throw new RuntimeException("Apenas o protético responsável pelo pedido pode solicitar terceirização");
            }

            // 5. Buscar o protético terceirizado
            Protetico proteticoTerceirizado = proteticoRepository.findById(request.getProteticoTerceirizadoId())
                    .orElseThrow(() -> new RuntimeException("Protético terceirizado não encontrado: " +
                            request.getProteticoTerceirizadoId()));

            // 6. Verificar se não é terceirização para si mesmo
            if (proteticoId.equals(request.getProteticoTerceirizadoId())) {
                throw new RuntimeException("Não é possível terceirizar para si mesmo");
            }

            // 7. NOVA VERIFICAÇÃO: Verificar se o protético terceirizado oferece o serviço e permite terceirização
            ServicoProtetico servicoDoProtetico = servicoProteticoRepository
                    .findByProteticoIdAndTipoServico(
                            request.getProteticoTerceirizadoId(),
                            pedido.getTipoServico()
                    )
                    .orElseThrow(() -> new RuntimeException(
                            "Este protético não oferece o serviço: " + pedido.getTipoServico()
                    ));

            if (!(servicoDoProtetico.getPoliticaExecucao() == PoliticaExecucaoServico.TERCEIRIZADO ||
                    servicoDoProtetico.getPoliticaExecucao() == PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO)) {
                throw new RuntimeException(
                        "Este protético não aceita terceirização para o serviço: " +
                                pedido.getTipoServico() +
                                ". Política: " + servicoDoProtetico.getPoliticaExecucao()
                );
            }
            if (!servicoDoProtetico.isAtivo()) {
                throw new RuntimeException("O serviço não está ativo para terceirização");
            }

            // 8. Verificar se o pedido pode ser terceirizado (usando método do Pedido)
            if (!pedido.podeSerTerceirizado()) {
                throw new RuntimeException("Este pedido não pode ser terceirizado no momento");
            }

            // 9. Verificar se já existe terceirização em andamento no pedido
            if (pedido.isTerceirizado() && pedido.isTerceirizacaoAtiva()) {
                throw new RuntimeException("Já existe uma terceirização em andamento para este pedido");
            }

            // 10. Verificar se já existe solicitação para o mesmo protético (evitar duplicatas)
            Optional<Terceirizacao> solicitacaoDuplicada = terceirizacaoRepository
                    .findByPedidoIdAndProteticoDestinoId(pedidoId, request.getProteticoTerceirizadoId());

            if (solicitacaoDuplicada.isPresent()) {
                Terceirizacao existente = solicitacaoDuplicada.get();
                // Verifica se está ativa
                if (existente.getStatus() == StatusTerceirizacao.SOLICITADO ||
                        existente.getStatus() == StatusTerceirizacao.ACEITO ||
                        existente.getStatus() == StatusTerceirizacao.EM_ANDAMENTO) {
                    throw new RuntimeException("Já existe uma solicitação de terceirização para este protético");
                }
            }

            // 11. Verificar percentual (se aplicável)
            if (request.getPercentual() != null) {
                if (request.getPercentual().compareTo(BigDecimal.ZERO) <= 0 ||
                        request.getPercentual().compareTo(new BigDecimal("100")) > 0) {
                    throw new RuntimeException("Percentual deve estar entre 0 e 100");
                }
            }

            System.out.println("✅ Validações passadas. Criando terceirização...");

            // 12. Usar o método do Pedido para atualizar seus campos internos
            pedido.solicitarTerceirizacao(
                    proteticoTerceirizado,
                    request.getPercentual(),
                    request.getTipo(),
                    request.getMotivo()
            );

            // 13. Salvar o pedido atualizado
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            System.out.println("✅ Pedido atualizado com terceirização! Status: " +
                    pedidoAtualizado.getStatusTerceirizacao());

            // 14. Criar registro na tabela terceirizacoes
            Terceirizacao terceirizacao = new Terceirizacao();
            terceirizacao.setProteticoOrigem(proteticoSolicitante);
            terceirizacao.setProteticoDestino(proteticoTerceirizado);
            terceirizacao.setPedido(pedidoAtualizado);
            terceirizacao.setServicoSolicitado(request.getDescricaoServico() != null ?
                    request.getDescricaoServico() : pedido.getTipoServico().getValorJson());
            terceirizacao.setObservacoes(request.getMotivo());
            terceirizacao.setStatus(StatusTerceirizacao.SOLICITADO);

            // Calcular valor se tiver percentual
            if (request.getPercentual() != null && pedido.getValorCobrado() != null) {
                BigDecimal valorCalculado = pedido.getValorCobrado()
                        .multiply(request.getPercentual())
                        .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                terceirizacao.setValorCombinado(valorCalculado.doubleValue());
            }

            // 15. Salvar a terceirização
            Terceirizacao terceirizacaoSalva = terceirizacaoRepository.save(terceirizacao);

            System.out.println("✅ Registro de terceirização criado com ID: " + terceirizacaoSalva.getId());

            // 16. Criar DTO de resposta (AJUSTADO PARA SUA ESTRUTURA)
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO();
            response.setId(terceirizacaoSalva.getId());
            response.setPedidoId(pedidoId);
            response.setCodigoPedido(pedido.getCodigo());

            // Criar DTO do protético solicitante
            ProteticoSimplesDTO proteticoSolicitanteDTO = new ProteticoSimplesDTO();
            proteticoSolicitanteDTO.setId(proteticoSolicitante.getId());
            proteticoSolicitanteDTO.setNome(proteticoSolicitante.getNome());
            proteticoSolicitanteDTO.setEmail(proteticoSolicitante.getEmail());
            proteticoSolicitanteDTO.setRegistroProfissional(proteticoSolicitante.getRegistroProfissional());
            response.setProteticoSolicitante(proteticoSolicitanteDTO);

            // Criar DTO do protético executor
            ProteticoSimplesDTO proteticoExecutorDTO = new ProteticoSimplesDTO();
            proteticoExecutorDTO.setId(proteticoTerceirizado.getId());
            proteticoExecutorDTO.setNome(proteticoTerceirizado.getNome());
            proteticoExecutorDTO.setEmail(proteticoTerceirizado.getEmail());
            proteticoExecutorDTO.setRegistroProfissional(proteticoTerceirizado.getRegistroProfissional());
            response.setProteticoExecutor(proteticoExecutorDTO);

            // Outros campos
            response.setPercentualTerceirizado(request.getPercentual());
            response.setTipoTerceirizacao(request.getTipo());
            response.setStatus(StatusTerceirizacao.SOLICITADO);
            response.setMotivo(request.getMotivo());
            response.setDescricaoServico(request.getDescricaoServico());
            response.setDataSolicitacao(terceirizacaoSalva.getSolicitadoEm());

            // Calcular valor terceirizado
            if (request.getPercentual() != null && pedido.getValorCobrado() != null) {
                BigDecimal valorTerceirizado = pedido.getValorCobrado()
                        .multiply(request.getPercentual())
                        .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
                response.setValorTerceirizado(valorTerceirizado);
            }

            System.out.println("✅ Terceirização solicitada com sucesso!");
            System.out.println("📊 Resposta: " + response);

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao solicitar terceirização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao solicitar terceirização: " + e.getMessage());
        }
    }

    // ============ MÉTODOS DE GERENCIAMENTO DE TERCEIRIZAÇÃO ============

    /**
     * Aceita uma terceirização (protético terceirizado)
     */
    public TerceirizacaoResponseDTO aceitarTerceirizacao(Long pedidoId, Long proteticoId) {
        System.out.println("🔄 aceitarTerceirizacao");
        System.out.println("📝 pedidoId: " + pedidoId + ", proteticoId (terceirizado): " + proteticoId);

        try {
            // 1. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 2. Buscar o protético terceirizado
            Protetico proteticoTerceirizado = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado: " + proteticoId));

            // 3. Verificar se o pedido está terceirizado para este protético
            if (!pedido.isTerceirizado() ||
                    pedido.getProteticoTerceirizado() == null ||
                    !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
                throw new RuntimeException("Esta terceirização não existe ou não é para você");
            }

            // 4. Verificar se o status permite aceitar
            if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.SOLICITADO) {
                throw new RuntimeException("Esta terceirização não pode ser aceita no momento. Status atual: " +
                        pedido.getStatusTerceirizacao());
            }

            // 5. Usar o método do Pedido para aceitar
            pedido.aceitarTerceirizacao();

            // 6. Salvar o pedido atualizado
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            // 7. Atualizar a entidade Terceirizacao se existir
            Optional<Terceirizacao> terceirizacaoOpt =
                    terceirizacaoRepository.findFirstByPedidoIdOrderByIdDesc(pedidoId);

            if (terceirizacaoOpt.isPresent()) {
                Terceirizacao terceirizacao = terceirizacaoOpt.get();
                terceirizacao.setStatus(StatusTerceirizacao.ACEITO);
                terceirizacao.setAceitoEm(LocalDateTime.now());
                terceirizacaoRepository.save(terceirizacao);
            }

            // 8. Criar DTO de resposta
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO(pedidoAtualizado);
            response.setId(terceirizacaoOpt.map(Terceirizacao::getId).orElse(null));

            System.out.println("✅ Terceirização aceita com sucesso!");

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao aceitar terceirização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao aceitar terceirização: " + e.getMessage());
        }
    }

    /**
     * Recusa uma terceirização (protético terceirizado)
     */
    public TerceirizacaoResponseDTO recusarTerceirizacao(Long pedidoId, Long proteticoId, String motivo) {
        System.out.println("🔄 recusarTerceirizacao");
        System.out.println("📝 pedidoId: " + pedidoId + ", proteticoId (terceirizado): " + proteticoId);
        System.out.println("📝 motivo: " + motivo);

        try {
            // 1. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 2. Buscar o protético terceirizado
            Protetico proteticoTerceirizado = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado: " + proteticoId));

            // 3. Verificar se o pedido está terceirizado para este protético
            if (!pedido.isTerceirizado() ||
                    pedido.getProteticoTerceirizado() == null ||
                    !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
                throw new RuntimeException("Esta terceirização não existe ou não é para você");
            }

            // 4. Verificar se o status permite recusar
            if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.SOLICITADO) {
                throw new RuntimeException("Esta terceirização não pode ser recusada no momento. Status atual: " +
                        pedido.getStatusTerceirizacao());
            }

            // 5. Adicionar motivo se fornecido
            if (motivo != null && !motivo.trim().isEmpty()) {
                pedido.setMotivoTerceirizacao(
                        (pedido.getMotivoTerceirizacao() != null ?
                                pedido.getMotivoTerceirizacao() + " | Recusa: " : "Recusa: ") + motivo
                );
            }

            // 6. Usar o método do Pedido para recusar
            pedido.recusarTerceirizacao();

            // 7. Salvar o pedido atualizado
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            // 8. Atualizar a entidade Terceirizacao se existir
            Optional<Terceirizacao> terceirizacaoOpt =
                    terceirizacaoRepository.findFirstByPedidoIdOrderByIdDesc(pedidoId);

            if (terceirizacaoOpt.isPresent()) {
                Terceirizacao terceirizacao = terceirizacaoOpt.get();
                terceirizacao.setStatus(StatusTerceirizacao.RECUSADO);
                terceirizacao.setObservacoes(
                        (terceirizacao.getObservacoes() != null
                                ? terceirizacao.getObservacoes() + " | Recusado: "
                                : "Recusado: ") + motivo
                );
                terceirizacaoRepository.save(terceirizacao);
            }

            // 9. Criar DTO de resposta
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO(pedidoAtualizado);
            response.setId(terceirizacaoOpt.map(Terceirizacao::getId).orElse(null));

            System.out.println("✅ Terceirização recusada com sucesso!");

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao recusar terceirização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao recusar terceirização: " + e.getMessage());
        }
    }

    /**
     * Inicia a execução da terceirização (protético terceirizado)
     */
    public TerceirizacaoResponseDTO iniciarTerceirizacao(Long pedidoId, Long proteticoId) {
        System.out.println("🔄 iniciarTerceirizacao");
        System.out.println("📝 pedidoId: " + pedidoId + ", proteticoId (terceirizado): " + proteticoId);

        try {
            // 1. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 2. Buscar o protético terceirizado
            Protetico proteticoTerceirizado = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado: " + proteticoId));

            // 3. Verificar se o pedido está terceirizado para este protético
            if (!pedido.isTerceirizado() ||
                    pedido.getProteticoTerceirizado() == null ||
                    !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
                throw new RuntimeException("Esta terceirização não existe ou não é para você");
            }

            // 4. Verificar se o status permite iniciar
            if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.ACEITO) {
                throw new RuntimeException("Esta terceirização não pode ser iniciada no momento. Status atual: " +
                        pedido.getStatusTerceirizacao());
            }

            // 5. Usar o método do Pedido para iniciar
            pedido.iniciarTerceirizacao();

            // 6. Salvar o pedido atualizado
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            // 7. Atualizar a entidade Terceirizacao se existir
            Optional<Terceirizacao> terceirizacaoOpt =
                    terceirizacaoRepository.findFirstByPedidoIdOrderByIdDesc(pedidoId);

            if (terceirizacaoOpt.isPresent()) {
                Terceirizacao terceirizacao = terceirizacaoOpt.get();
                terceirizacao.setStatus(StatusTerceirizacao.EM_ANDAMENTO);
                terceirizacaoRepository.save(terceirizacao);
            }

            // 8. Criar DTO de resposta
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO(pedidoAtualizado);
            response.setId(terceirizacaoOpt.map(Terceirizacao::getId).orElse(null));

            System.out.println("✅ Execução da terceirização iniciada!");

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao iniciar terceirização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao iniciar terceirização: " + e.getMessage());
        }
    }

    /**
     * Conclui a terceirização (protético terceirizado)
     */
    public TerceirizacaoResponseDTO concluirTerceirizacao(Long pedidoId, Long proteticoId) {
        System.out.println("🔄 concluirTerceirizacao");
        System.out.println("📝 pedidoId: " + pedidoId + ", proteticoId (terceirizado): " + proteticoId);

        try {
            // 1. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 2. Buscar o protético terceirizado
            Protetico proteticoTerceirizado = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado: " + proteticoId));

            // 3. Verificar se o pedido está terceirizado para este protético
            if (!pedido.isTerceirizado() ||
                    pedido.getProteticoTerceirizado() == null ||
                    !pedido.getProteticoTerceirizado().getId().equals(proteticoId)) {
                throw new RuntimeException("Esta terceirização não existe ou não é para você");
            }

            // 4. Verificar se o status permite concluir
            if (pedido.getStatusTerceirizacao() != StatusTerceirizacao.EM_ANDAMENTO) {
                throw new RuntimeException("Esta terceirização não pode ser concluída no momento. Status atual: " +
                        pedido.getStatusTerceirizacao());
            }

            // 5. Usar o método do Pedido para concluir
            pedido.concluirTerceirizacao();

            // 6. Salvar o pedido atualizado
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            // 7. Atualizar a entidade Terceirizacao se existir
            Optional<Terceirizacao> terceirizacaoOpt =
                    terceirizacaoRepository.findFirstByPedidoIdOrderByIdDesc(pedidoId);

            if (terceirizacaoOpt.isPresent()) {
                Terceirizacao terceirizacao = terceirizacaoOpt.get();
                terceirizacao.setStatus(StatusTerceirizacao.CONCLUIDO);
                terceirizacao.setConcluidoEm(LocalDateTime.now());
                terceirizacaoRepository.save(terceirizacao);
            }

            // 8. Criar DTO de resposta
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO(pedidoAtualizado);
            response.setId(terceirizacaoOpt.map(Terceirizacao::getId).orElse(null));

            System.out.println("✅ Terceirização concluída com sucesso!");

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao concluir terceirização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao concluir terceirização: " + e.getMessage());
        }
    }

    /**
     * Cancela uma terceirização (qualquer protético envolvido)
     */
    public TerceirizacaoResponseDTO cancelarTerceirizacao(Long pedidoId, Long proteticoId, String motivo) {
        System.out.println("🔄 cancelarTerceirizacao");
        System.out.println("📝 pedidoId: " + pedidoId + ", proteticoId: " + proteticoId);
        System.out.println("📝 motivo: " + motivo);

        try {
            // 1. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 2. Buscar o protético
            Protetico protetico = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado: " + proteticoId));

            // 3. Verificar se o protético tem permissão para cancelar
            // Pode cancelar se for o protético solicitante OU o protético terceirizado
            boolean isSolicitante = pedido.getProtetico() != null &&
                    pedido.getProtetico().getId().equals(proteticoId);
            boolean isTerceirizado = pedido.getProteticoTerceirizado() != null &&
                    pedido.getProteticoTerceirizado().getId().equals(proteticoId);

            if (!isSolicitante && !isTerceirizado) {
                throw new RuntimeException("Você não tem permissão para cancelar esta terceirização");
            }

            // 4. Verificar se o status permite cancelar
            StatusTerceirizacao statusAtual = pedido.getStatusTerceirizacao();
            if (statusAtual == StatusTerceirizacao.CONCLUIDO ||
                    statusAtual == StatusTerceirizacao.CANCELADO ||
                    statusAtual == StatusTerceirizacao.RECUSADO) {
                throw new RuntimeException("Esta terceirização não pode ser cancelada. Status atual: " + statusAtual);
            }

            // 5. Adicionar motivo se fornecido
            if (motivo != null && !motivo.trim().isEmpty()) {
                String prefixo = isSolicitante ? "Cancelado pelo solicitante: " : "Cancelado pelo executor: ";
                pedido.setMotivoTerceirizacao(
                        (pedido.getMotivoTerceirizacao() != null ?
                                pedido.getMotivoTerceirizacao() + " | " + prefixo : prefixo) + motivo
                );
            }

            // 6. Usar o método do Pedido para cancelar
            pedido.cancelarTerceirizacao();

            // 7. Salvar o pedido atualizado
            Pedido pedidoAtualizado = pedidoRepository.save(pedido);

            // 8. Atualizar a entidade Terceirizacao se existir
            Optional<Terceirizacao> terceirizacaoOpt = terceirizacaoRepository.findTopByPedidoId(pedidoId);
            if (terceirizacaoOpt.isPresent()) {
                Terceirizacao terceirizacao = terceirizacaoOpt.get();
                terceirizacao.setStatus(StatusTerceirizacao.CANCELADO);
                terceirizacao.setObservacoes(
                        (terceirizacao.getObservacoes() != null ?
                                terceirizacao.getObservacoes() + " | Cancelado: " : "Cancelado: ") + motivo
                );
                terceirizacaoRepository.save(terceirizacao);
            }

            // 9. Criar DTO de resposta
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO(pedidoAtualizado);
            response.setId(terceirizacaoOpt.map(Terceirizacao::getId).orElse(null));

            System.out.println("✅ Terceirização cancelada com sucesso!");

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao cancelar terceirização: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao cancelar terceirização: " + e.getMessage());
        }
    }

    /**
     * Busca terceirização por ID do pedido
     */
    public TerceirizacaoResponseDTO buscarPorPedidoId(Long pedidoId) {
        System.out.println("🔄 buscarPorPedidoId: " + pedidoId);

        try {
            // 1. Buscar o pedido
            Pedido pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new RuntimeException("Pedido não encontrado: " + pedidoId));

            // 2. Verificar se o pedido está terceirizado
            if (!pedido.isTerceirizado()) {
                throw new RuntimeException("Este pedido não está terceirizado");
            }

            // 3. Buscar a entidade Terceirizacao se existir
            Optional<Terceirizacao> terceirizacaoOpt = terceirizacaoRepository.findTopByPedidoId(pedidoId);

            // 4. Criar DTO de resposta
            TerceirizacaoResponseDTO response = new TerceirizacaoResponseDTO(pedido);
            response.setId(terceirizacaoOpt.map(Terceirizacao::getId).orElse(null));

            System.out.println("✅ Terceirização encontrada para pedido: " + pedidoId);

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar terceirização: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar terceirização: " + e.getMessage());
        }
    }

    /**
     * Lista todas terceirizações de um protético
     */
    public List<TerceirizacaoResponseDTO> listarTerceirizacoesPorProtetico(Long proteticoId) {
        System.out.println("🔄 listarTerceirizacoesPorProtetico: " + proteticoId);

        try {
            // 1. Buscar o protético
            Protetico protetico = proteticoRepository.findById(proteticoId)
                    .orElseThrow(() -> new RuntimeException("Protético não encontrado: " + proteticoId));

            // 2. Buscar pedidos onde o protético está envolvido
            List<Pedido> pedidosComoOrigem = pedidoRepository.findByProteticoId(proteticoId);
            List<Pedido> pedidosComoDestino = pedidoRepository.findByProteticoTerceirizadoId(proteticoId);

            // 3. Combinar listas e remover duplicados
            List<Pedido> todosPedidos = new ArrayList<>();
            todosPedidos.addAll(pedidosComoOrigem);
            todosPedidos.addAll(pedidosComoDestino);

            // Filtrar apenas pedidos terceirizados
            List<Pedido> pedidosTerceirizados = todosPedidos.stream()
                    .filter(p -> p.isTerceirizado())
                    .distinct()
                    .collect(Collectors.toList());

            if (pedidosTerceirizados.isEmpty()) {
                return new ArrayList<>();
            }

            // 4. Buscar IDs dos pedidos
            List<Long> pedidoIds = pedidosTerceirizados.stream()
                    .map(Pedido::getId)
                    .collect(Collectors.toList());

            // 5. Buscar terceirizações usando o novo método
            List<Terceirizacao> terceirizacoes = terceirizacaoRepository.findByPedidoIdIn(pedidoIds);

            // 6. Criar mapa para acesso rápido
            Map<Long, Terceirizacao> terceirizacoesMap = terceirizacoes.stream()
                    .collect(Collectors.toMap(t -> t.getPedido().getId(), t -> t));

            // 7. Criar DTOs de resposta
            List<TerceirizacaoResponseDTO> response = pedidosTerceirizados.stream()
                    .map(pedido -> {
                        TerceirizacaoResponseDTO dto = new TerceirizacaoResponseDTO(pedido);
                        Terceirizacao terceirizacao = terceirizacoesMap.get(pedido.getId());
                        if (terceirizacao != null) {
                            dto.setId(terceirizacao.getId());
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            System.out.println("✅ Encontradas " + response.size() + " terceirizações para protético: " + proteticoId);

            return response;

        } catch (Exception e) {
            System.err.println("❌ Erro ao listar terceirizações: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao listar terceirizações: " + e.getMessage());
        }
    }

    // ============ MÉTODOS NOVOS ADICIONADOS ============

    /**
     * NOVO MÉTODO: Lista protéticos disponíveis por tipo de serviço (String)
     * Usado pelo endpoint /disponiveis quando tipoServico vem como String
     */
    public List<ProteticoSimplesDTO> listarProteticosDisponiveisPorTipoServico(
            Long pedidoId,
            String tipoServicoStr) {

        System.out.println("🔄 listarProteticosDisponiveisPorTipoServico");
        System.out.println("📝 pedidoId: " + pedidoId + ", tipoServicoStr: " + tipoServicoStr);

        // Converte String para TipoServico
        TipoServico tipoServico = TipoServico.fromValue(tipoServicoStr);
        if (tipoServico == null) {
            throw new RuntimeException("Tipo de serviço inválido: " + tipoServicoStr);
        }

        System.out.println("✅ Tipo convertido: " + tipoServico);

        // Chama o método existente (que já está funcionando)
        return listarProteticosDisponiveis(pedidoId, tipoServico);
    }

    /**
     * NOVO MÉTODO: Lista protéticos por tipo de serviço usando sistema de serviços
     * Busca protéticos que realmente oferecem o serviço (ativos)
     */
    public List<ProteticoSimplesDTO> listarProteticosPorServicoAtivos(
            String tipoServicoStr,
            Long excluirProteticoId) {

        System.out.println("🔄 listarProteticosPorServicoAtivos");
        System.out.println("📝 tipoServicoStr: " + tipoServicoStr);
        System.out.println("📝 excluirProteticoId: " + excluirProteticoId);

        // Converte String para TipoServico
        TipoServico tipoServico = TipoServico.fromValue(tipoServicoStr);
        if (tipoServico == null) {
            throw new RuntimeException("Tipo de serviço inválido: " + tipoServicoStr);
        }

        System.out.println("✅ Tipo convertido: " + tipoServico);

        try {
            // 1. Primeiro tenta usar o sistema de serviços
            if (servicoProteticoService != null) {
                System.out.println("🔍 Buscando via ServicoProteticoService...");

                List<ServicoProteticoDTO> servicos = servicoProteticoService
                        .buscarProteticosPorServico(tipoServico);

                System.out.println("📊 Serviços encontrados: " + servicos.size());

                // Filtra apenas serviços ativos
                List<ServicoProteticoDTO> servicosAtivos = servicos.stream()
                        .filter(ServicoProteticoDTO::isAtivo)
                        .collect(Collectors.toList());

                System.out.println("✅ Serviços ativos: " + servicosAtivos.size());

                // Remove protético especificado (se houver)
                if (excluirProteticoId != null) {
                    final Long excluirIdFinal = excluirProteticoId;
                    servicosAtivos = servicosAtivos.stream()
                            .filter(s -> !excluirIdFinal.equals(s.getProteticoId()))
                            .collect(Collectors.toList());

                    System.out.println("✅ Após exclusão: " + servicosAtivos.size());
                }

                // Converte para ProteticoSimplesDTO
                return servicosAtivos.stream()
                        .map(this::convertServicoParaProteticoSimples)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.out.println("❌ Erro no sistema de serviços: " + e.getMessage());
            e.printStackTrace();
        }

        // 2. Fallback: usa a lógica atualizada (baseada em serviços)
        System.out.println("🔄 Usando fallback (busca por serviços)...");

        // Cria cópias finais para usar nas lambdas
        final TipoServico tipoFinal = tipoServico;
        final Long excluirIdFinal = excluirProteticoId;

        List<ProteticoSimplesDTO> resultado = proteticoRepository.findAll().stream()
                .filter(p -> {
                    // Verifica se tem serviços que permitem terceirização para este tipo
                    return p.getServicosProtetico().stream()
                            .anyMatch(servico ->
                                    servico.isAtivo() &&
                                            servico.getTipoServico() == tipoFinal &&
                                            servico.getPoliticaExecucao() != null &&
                                            (servico.getPoliticaExecucao() == PoliticaExecucaoServico.TERCEIRIZADO ||
                                                    servico.getPoliticaExecucao() == PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO)  // ← CORREÇÃO
                            );
                })
                .filter(p -> excluirIdFinal == null || !p.getId().equals(excluirIdFinal))
                .map(p -> {
                    ProteticoSimplesDTO dto = new ProteticoSimplesDTO();
                    dto.setId(p.getId());
                    dto.setNome(p.getNome());
                    dto.setEmail(p.getEmail());
                    dto.setEspecializacao(p.getEspecializacao());
                    dto.setRegistroProfissional(p.getRegistroProfissional());
                    dto.setNotaTerceirizacao(p.getNotaTerceirizacao());
                    dto.setQuantidadeTerceirizacoes(p.getQuantidadeTerceirizacoes());
                    dto.setTaxaMinimaTerceirizacao(p.getTaxaMinimaTerceirizacao());
                    return dto;
                })
                .collect(Collectors.toList());

        System.out.println("✅ Fallback encontrou: " + resultado.size() + " protéticos");
        return resultado;
    }

    /**
     * NOVO MÉTODO: Converte ServicoProteticoDTO para ProteticoSimplesDTO
     */
    private ProteticoSimplesDTO convertServicoParaProteticoSimples(ServicoProteticoDTO servico) {
        ProteticoSimplesDTO dto = new ProteticoSimplesDTO();

        // Informações do protético
        dto.setId(servico.getProteticoId());
        dto.setNome(servico.getProteticoNome());

        // Informações do serviço
        dto.setEspecializacao(servico.getDescricao());

        // Busca informações adicionais do protético
        try {
            Protetico protetico = proteticoRepository.findById(servico.getProteticoId()).orElse(null);
            if (protetico != null) {
                dto.setEmail(protetico.getEmail());
                dto.setRegistroProfissional(protetico.getRegistroProfissional());
                dto.setNotaTerceirizacao(protetico.getNotaTerceirizacao());
                dto.setQuantidadeTerceirizacoes(protetico.getQuantidadeTerceirizacoes());
                dto.setTaxaMinimaTerceirizacao(protetico.getTaxaMinimaTerceirizacao());
            }
        } catch (Exception e) {
            System.out.println("⚠️ Não foi possível buscar detalhes do protético: " + e.getMessage());
        }

        return dto;
    }

    /**
     * NOVO MÉTODO: Busca simplificada - apenas por tipo de serviço
     */
    public List<ProteticoSimplesDTO> buscarProteticosSimplesPorServico(String tipoServicoStr) {
        System.out.println("🔄 buscarProteticosSimplesPorServico: " + tipoServicoStr);

        // Converte String para TipoServico
        TipoServico tipoServico = TipoServico.fromValue(tipoServicoStr);
        if (tipoServico == null) {
            throw new RuntimeException("Tipo de serviço inválido: " + tipoServicoStr);
        }

        // Usa o sistema de serviços se disponível
        try {
            return listarProteticosPorServicoAtivos(tipoServicoStr, null);
        } catch (Exception e) {
            System.out.println("⚠️ Fallback para método original: " + e.getMessage());

            // Fallback: busca todos e filtra por serviços
            List<Protetico> todos = proteticoRepository.findAll();

            // Cria cópia final para usar na lambda
            final TipoServico tipoFinal = tipoServico;

            return todos.stream()
                    .filter(p -> {
                        return p.getServicosProtetico().stream()
                                .anyMatch(servico ->
                                        servico.isAtivo() &&
                                                servico.getTipoServico() == tipoFinal &&
                                                servico.getPoliticaExecucao() != null &&
                                                (servico.getPoliticaExecucao() == PoliticaExecucaoServico.TERCEIRIZADO ||
                                                        servico.getPoliticaExecucao() == PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO)  // ← CORREÇÃO
                                );
                    })
                    .map(p -> {
                        ProteticoSimplesDTO dto = new ProteticoSimplesDTO();
                        dto.setId(p.getId());
                        dto.setNome(p.getNome());
                        dto.setEmail(p.getEmail());
                        dto.setEspecializacao(p.getEspecializacao());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }
}