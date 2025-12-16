package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.DTO.ServicoProteticoDTO;
import com.proteticos.ordermanagement.DTO.ServicoProteticoRequestDTO;
import com.proteticos.ordermanagement.DTO.AtualizarServicoRequestDTO;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.model.ServicoProtetico;
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.model.PoliticaExecucaoServico; // NOVO IMPORT AQUI
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import com.proteticos.ordermanagement.repository.ServicoProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoProteticoService {

    @Autowired
    private ServicoProteticoRepository servicoProteticoRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    // POLÍTICAS CONSTANTES PARA REUSO
    private static final List<PoliticaExecucaoServico> POLITICAS_TERCEIRIZACAO = Arrays.asList(
            PoliticaExecucaoServico.TERCEIRIZADO,
            PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO
    );

    private static final List<PoliticaExecucaoServico> POLITICAS_EXECUTORES = Arrays.asList(
            PoliticaExecucaoServico.PROPRIO,
            PoliticaExecucaoServico.PROPRIO_OU_TERCEIRIZADO
    );

    @Transactional
    public ServicoProteticoDTO adicionarOuAtualizarServico(Long proteticoId, ServicoProteticoRequestDTO servicoDTO) {
        Protetico protetico = proteticoRepository.findById(proteticoId)
                .orElseThrow(() -> new RuntimeException("Protético não encontrado"));

        Optional<ServicoProtetico> existente = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, servicoDTO.getTipoServico());

        ServicoProtetico servico;
        if (existente.isPresent()) {
            // ATUALIZA serviço existente
            servico = existente.get();
            atualizarServicoExistente(servico, servicoDTO);
        } else {
            // CRIA novo serviço
            servico = criarNovoServico(protetico, servicoDTO);
        }

        ServicoProtetico saved = servicoProteticoRepository.save(servico);
        return convertToDTO(saved);
    }

    // MÉTODO: Criar novo serviço com terceirização
    private ServicoProtetico criarNovoServico(Protetico protetico, ServicoProteticoRequestDTO dto) {
        ServicoProtetico servico = new ServicoProtetico();
        servico.setProtetico(protetico);
        servico.setTipoServico(dto.getTipoServico());
        servico.setPreco(dto.getPreco());
        servico.setDescricao(dto.getDescricao());

        // Converte dias para horas
        if (dto.getTempoMedioDias() != null) {
            servico.setTempoMedioDias(dto.getTempoMedioDias());
        }

        servico.setAtivo(dto.isAtivo());

        // Configuração de terceirização
        servico.setPoliticaExecucao(dto.getPoliticaExecucao());
        servico.setPrecoTerceirizado(dto.getPrecoTerceirizado());
        servico.setObservacoesTerceirizacao(dto.getObservacoesTerceirizacao());
        servico.setTerceirizadoPreferidoId(dto.getTerceirizadoPreferidoId());

        // Converte prazo de terceirização (dias para horas)
        if (dto.getPrazoTerceirizadoDias() != null) {
            servico.setPrazoTerceirizadoDias(dto.getPrazoTerceirizadoDias());
        }

        return servico;
    }

    // MÉTODO: Atualizar serviço existente com terceirização
    private void atualizarServicoExistente(ServicoProtetico servico, ServicoProteticoRequestDTO dto) {
        servico.setPreco(dto.getPreco());
        servico.setDescricao(dto.getDescricao());

        // Converte dias para horas
        if (dto.getTempoMedioDias() != null) {
            servico.setTempoMedioDias(dto.getTempoMedioDias());
        }

        servico.setAtivo(dto.isAtivo());

        // Atualiza configuração de terceirização
        servico.setPoliticaExecucao(dto.getPoliticaExecucao());
        servico.setPrecoTerceirizado(dto.getPrecoTerceirizado());
        servico.setObservacoesTerceirizacao(dto.getObservacoesTerceirizacao());
        servico.setTerceirizadoPreferidoId(dto.getTerceirizadoPreferidoId());

        // Converte prazo de terceirização (dias para horas)
        if (dto.getPrazoTerceirizadoDias() != null) {
            servico.setPrazoTerceirizadoDias(dto.getPrazoTerceirizadoDias());
        }
    }

    // MÉTODO para atualizar serviço completo com terceirização
    @Transactional
    public ServicoProteticoDTO atualizarServico(Long proteticoId, TipoServico tipoServico, AtualizarServicoRequestDTO requestDTO) {
        ServicoProtetico servico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        if (requestDTO.getPreco() != null) {
            servico.setPreco(requestDTO.getPreco());
        }

        if (requestDTO.getDescricao() != null) {
            servico.setDescricao(requestDTO.getDescricao());
        }

        // Converte dias para horas
        if (requestDTO.getTempoMedioDias() != null) {
            servico.setTempoMedioDias(requestDTO.getTempoMedioDias());
        }

        // Atualiza campos de terceirização
        if (requestDTO.getPoliticaExecucao() != null) {
            servico.setPoliticaExecucao(requestDTO.getPoliticaExecucao());
        }

        if (requestDTO.getPrecoTerceirizado() != null) {
            servico.setPrecoTerceirizado(requestDTO.getPrecoTerceirizado());
        }

        if (requestDTO.getObservacoesTerceirizacao() != null) {
            servico.setObservacoesTerceirizacao(requestDTO.getObservacoesTerceirizacao());
        }

        if (requestDTO.getTerceirizadoPreferidoId() != null) {
            servico.setTerceirizadoPreferidoId(requestDTO.getTerceirizadoPreferidoId());
        }

        // Converte prazo de terceirização
        if (requestDTO.getPrazoTerceirizadoDias() != null) {
            servico.setPrazoTerceirizadoDias(requestDTO.getPrazoTerceirizadoDias());
        }

        ServicoProtetico updated = servicoProteticoRepository.save(servico);
        return convertToDTO(updated);
    }

    // ============ MÉTODOS EXISTENTES ============
    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> listarServicosPorProtetico(Long proteticoId) {
        return servicoProteticoRepository.findByProteticoId(proteticoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> listarServicosAtivosPorProtetico(Long proteticoId) {
        return servicoProteticoRepository.findByProteticoIdAndAtivoTrue(proteticoId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServicoProteticoDTO buscarServicoPorTipo(Long proteticoId, TipoServico tipoServico) {
        Optional<ServicoProtetico> servico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico);
        return servico.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public ServicoProteticoDTO atualizarPrecoServico(Long proteticoId, TipoServico tipoServico, BigDecimal novoPreco) {
        ServicoProtetico servico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        servico.setPreco(novoPreco);
        ServicoProtetico updated = servicoProteticoRepository.save(servico);
        return convertToDTO(updated);
    }

    @Transactional
    public ServicoProteticoDTO alterarStatusServico(Long proteticoId, TipoServico tipoServico, boolean ativo) {
        ServicoProtetico servico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        servico.setAtivo(ativo);
        ServicoProtetico updated = servicoProteticoRepository.save(servico);
        return convertToDTO(updated);
    }

    @Transactional
    public void removerServico(Long proteticoId, TipoServico tipoServico) {
        ServicoProtetico servico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        servicoProteticoRepository.delete(servico);
    }

    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> buscarProteticosPorServico(TipoServico tipoServico) {
        return servicoProteticoRepository.findByTipoServicoAndAtivoTrue(tipoServico).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ============ MÉTODOS PARA TERCEIRIZAÇÃO ============

    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> buscarServicosAceitamTerceirizacao(TipoServico tipoServico) {
        return servicoProteticoRepository
                .findByTipoServicoAndPoliticaExecucaoInAndAtivoTrue(tipoServico, POLITICAS_TERCEIRIZACAO)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> buscarProteticosParaTerceirizacao(Long proteticoId, TipoServico tipoServico) {
        // Busca protéticos que aceitam fazer esse serviço (exceto o próprio)
        List<ServicoProtetico> servicos = servicoProteticoRepository
                .findByTipoServicoAndAtivoTrue(tipoServico)
                .stream()
                .filter(s -> !s.getProtetico().getId().equals(proteticoId)) // Exclui o próprio
                .filter(s -> s.executaProprio()) // Filtra só quem executa
                .collect(Collectors.toList());

        return servicos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> buscarServicosQuePossoTerceirizar(Long proteticoId) {
        return servicoProteticoRepository
                .findByProteticoIdAndAtivoTrue(proteticoId)
                .stream()
                .filter(s -> s.aceitaTerceirizacao()) // Filtra só quem aceita terceirização
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean podeTerceirizarServico(Long proteticoId, TipoServico tipoServico) {
        Optional<ServicoProtetico> servico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico);

        return servico.isPresent() &&
                servico.get().isAtivo() &&
                servico.get().aceitaTerceirizacao();
    }

    // Método para sugerir terceirizado preferido
    @Transactional(readOnly = true)
    public Optional<ServicoProteticoDTO> sugerirTerceirizado(Long proteticoId, TipoServico tipoServico) {
        // Primeiro, verifica se o protético tem um terceirizado preferido para esse serviço
        Optional<ServicoProtetico> meuServico = servicoProteticoRepository
                .findByProteticoIdAndTipoServico(proteticoId, tipoServico);

        if (meuServico.isPresent() && meuServico.get().getTerceirizadoPreferidoId() != null) {
            // Busca o serviço do terceirizado preferido
            Optional<ServicoProtetico> terceirizadoPreferido = servicoProteticoRepository
                    .findByProteticoIdAndTipoServico(
                            meuServico.get().getTerceirizadoPreferidoId(),
                            tipoServico
                    );

            if (terceirizadoPreferido.isPresent() &&
                    terceirizadoPreferido.get().isAtivo() &&
                    terceirizadoPreferido.get().executaProprio()) {
                return Optional.of(convertToDTO(terceirizadoPreferido.get()));
            }
        }

        // Se não tem preferido ou preferido não está disponível, busca outros
        List<ServicoProteticoDTO> opcoes = buscarProteticosParaTerceirizacao(proteticoId, tipoServico);
        return opcoes.stream().findFirst();
    }

    // ============ MÉTODOS AUXILIARES ============

    private Integer convertDiasParaHoras(Integer dias) {
        if (dias == null) {
            return null;
        }
        return dias * 24;
    }

    private Integer convertHorasParaDias(Integer horas) {
        if (horas == null) {
            return null;
        }
        return (int) Math.ceil(horas / 24.0);
    }

    private ServicoProteticoDTO convertToDTO(ServicoProtetico servico) {
        return ServicoProteticoDTO.fromEntity(servico);
    }
}