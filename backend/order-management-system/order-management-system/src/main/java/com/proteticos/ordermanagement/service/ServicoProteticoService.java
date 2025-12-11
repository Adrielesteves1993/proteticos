package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.DTO.ServicoProteticoDTO;
import com.proteticos.ordermanagement.DTO.ServicoProteticoRequestDTO;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.model.ServicoProtetico;
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import com.proteticos.ordermanagement.repository.ServicoProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicoProteticoService {

    @Autowired
    private ServicoProteticoRepository servicoProteticoRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

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
            servico.setPreco(servicoDTO.getPreco());
            servico.setDescricao(servicoDTO.getDescricao());
            servico.setTempoMedioHoras(servicoDTO.getTempoMedioHoras());
            servico.setAtivo(servicoDTO.isAtivo());
        } else {
            // CRIA novo serviço
            servico = new ServicoProtetico();
            servico.setProtetico(protetico);
            servico.setTipoServico(servicoDTO.getTipoServico());
            servico.setPreco(servicoDTO.getPreco());
            servico.setDescricao(servicoDTO.getDescricao());
            servico.setTempoMedioHoras(servicoDTO.getTempoMedioHoras());
            servico.setAtivo(servicoDTO.isAtivo());
        }

        ServicoProtetico saved = servicoProteticoRepository.save(servico);
        return convertToDTO(saved);
    }

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

    // MÉTODO REMOVIDO - findByTipoServicoAndPrecoBetween não existe no Repository
    /*
    @Transactional(readOnly = true)
    public List<ServicoProteticoDTO> buscarServicosPorFaixaPreco(TipoServico tipoServico, Double precoMin, Double precoMax) {
        return servicoProteticoRepository.findByTipoServicoAndPrecoBetween(tipoServico, precoMin, precoMax).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    */

    private ServicoProteticoDTO convertToDTO(ServicoProtetico servico) {
        ServicoProteticoDTO dto = new ServicoProteticoDTO();
        dto.setId(servico.getId());
        dto.setTipoServico(servico.getTipoServico());
        dto.setPreco(servico.getPreco());
        dto.setDescricao(servico.getDescricao());
        dto.setTempoMedioHoras(servico.getTempoMedioHoras());
        dto.setAtivo(servico.isAtivo());
        dto.setDataCriacao(servico.getDataCriacao());
        dto.setDataAtualizacao(servico.getDataAtualizacao());

        if (servico.getProtetico() != null) {
            dto.setProteticoId(servico.getProtetico().getId());
            dto.setProteticoNome(servico.getProtetico().getNome());
        }

        return dto;
    }
}