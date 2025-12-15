// src/main/java/com/proteticos/ordermanagement/service/ProteticoService.java
package com.proteticos.ordermanagement.service;

import com.proteticos.ordermanagement.DTO.ProteticoDTO;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProteticoService {

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Transactional(readOnly = true)
    public List<ProteticoDTO> listarTodos() {
        return proteticoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProteticoDTO buscarPorId(Long id) {
        Optional<Protetico> protetico = proteticoRepository.findById(id);
        return protetico.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public ProteticoDTO criarProtetico(Protetico protetico) {
        // Verifica se email já existe
        if (proteticoRepository.findByEmail(protetico.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Verifica se registro profissional já existe
        if (protetico.getRegistroProfissional() != null &&
                proteticoRepository.existsByRegistroProfissional(protetico.getRegistroProfissional())) {
            throw new RuntimeException("Registro profissional já cadastrado");
        }

        Protetico saved = proteticoRepository.save(protetico);
        return convertToDTO(saved);
    }

    @Transactional
    public ProteticoDTO atualizarProtetico(Long id, ProteticoDTO proteticoDTO) {
        Protetico protetico = proteticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protético não encontrado"));

        // Atualiza campos básicos
        if (proteticoDTO.getNome() != null) {
            protetico.setNome(proteticoDTO.getNome());
        }
        if (proteticoDTO.getEmail() != null && !proteticoDTO.getEmail().equals(protetico.getEmail())) {
            // Verifica se novo email não está em uso
            Optional<Protetico> existente = proteticoRepository.findByEmail(proteticoDTO.getEmail());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new RuntimeException("Email já está em uso");
            }
            protetico.setEmail(proteticoDTO.getEmail());
        }
        if (proteticoDTO.getEspecializacao() != null) {
            protetico.setEspecializacao(proteticoDTO.getEspecializacao());
        }
        if (proteticoDTO.getRegistroProfissional() != null) {
            protetico.setRegistroProfissional(proteticoDTO.getRegistroProfissional());
        }

        // APENAS este campo existe no seu Protetico.java
        protetico.setAceitaTerceirizacao(proteticoDTO.isAceitaTerceirizacao());

        Protetico atualizado = proteticoRepository.save(protetico);
        return convertToDTO(atualizado);
    }

    @Transactional(readOnly = true)
    public List<ProteticoDTO> buscarPorEspecializacao(String especializacao) {
        return proteticoRepository.findByEspecializacaoContainingIgnoreCase(especializacao).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProteticoDTO> buscarPorNome(String nome) {
        return proteticoRepository.findByNomeContaining(nome).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void excluirProtetico(Long id) {
        if (!proteticoRepository.existsById(id)) {
            throw new RuntimeException("Protético não encontrado");
        }
        proteticoRepository.deleteById(id);
    }

    private ProteticoDTO convertToDTO(Protetico protetico) {
        ProteticoDTO dto = new ProteticoDTO();
        dto.setId(protetico.getId());
        dto.setNome(protetico.getNome());
        dto.setEmail(protetico.getEmail());
        dto.setRegistroProfissional(protetico.getRegistroProfissional());
        dto.setEspecializacao(protetico.getEspecializacao());
        dto.setAceitaTerceirizacao(protetico.isAceitaTerceirizacao());
        return dto;
    }
}