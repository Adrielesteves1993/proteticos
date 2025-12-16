package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.DTO.ServicoProteticoDTO;
import com.proteticos.ordermanagement.DTO.ServicoProteticoRequestDTO;
import com.proteticos.ordermanagement.DTO.AtualizarServicoRequestDTO;
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.service.ServicoProteticoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/servicos-protetico")
public class ServicoProteticoController {

    @Autowired
    private ServicoProteticoService servicoProteticoService;

    // ============ CRUD DE SERVIÇOS ============

    /**
     * Criar ou atualizar serviço de um protético
     * POST /api/servicos-protetico/protetico/{proteticoId}
     */
    @PostMapping("/protetico/{proteticoId}")
    public ResponseEntity<ServicoProteticoDTO> adicionarOuAtualizarServico(
            @PathVariable Long proteticoId,
            @RequestBody @Valid ServicoProteticoRequestDTO servicoDTO) {
        ServicoProteticoDTO saved = servicoProteticoService
                .adicionarOuAtualizarServico(proteticoId, servicoDTO);
        return ResponseEntity.ok(saved);
    }

    /**
     * Listar todos serviços de um protético
     * GET /api/servicos-protetico/protetico/{proteticoId}
     */
    @GetMapping("/protetico/{proteticoId}")
    public ResponseEntity<List<ServicoProteticoDTO>> listarServicosPorProtetico(
            @PathVariable Long proteticoId) {
        List<ServicoProteticoDTO> servicos = servicoProteticoService
                .listarServicosPorProtetico(proteticoId);
        return ResponseEntity.ok(servicos);
    }

    /**
     * Listar serviços ativos de um protético
     * GET /api/servicos-protetico/protetico/{proteticoId}/ativos
     */
    @GetMapping("/protetico/{proteticoId}/ativos")
    public ResponseEntity<List<ServicoProteticoDTO>> listarServicosAtivosPorProtetico(
            @PathVariable Long proteticoId) {
        List<ServicoProteticoDTO> servicos = servicoProteticoService
                .listarServicosAtivosPorProtetico(proteticoId);
        return ResponseEntity.ok(servicos);
    }

    /**
     * Buscar serviço específico por tipo
     * GET /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}
     */
    @GetMapping("/protetico/{proteticoId}/tipo/{tipoServico}")
    public ResponseEntity<ServicoProteticoDTO> buscarServicoPorTipo(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico) {
        ServicoProteticoDTO servico = servicoProteticoService
                .buscarServicoPorTipo(proteticoId, tipoServico);
        return servico != null ? ResponseEntity.ok(servico) : ResponseEntity.notFound().build();
    }

    /**
     * Atualizar preço de um serviço
     * PATCH /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}/preco
     */
    @PatchMapping("/protetico/{proteticoId}/tipo/{tipoServico}/preco")
    public ResponseEntity<ServicoProteticoDTO> atualizarPrecoServico(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico,
            @RequestParam BigDecimal novoPreco) {
        ServicoProteticoDTO updated = servicoProteticoService
                .atualizarPrecoServico(proteticoId, tipoServico, novoPreco);
        return ResponseEntity.ok(updated);
    }

    /**
     * Atualizar serviço completo (incluindo política de terceirização)
     * PUT /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}
     */
    @PutMapping("/protetico/{proteticoId}/tipo/{tipoServico}")
    public ResponseEntity<ServicoProteticoDTO> atualizarServicoCompleto(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico,
            @RequestBody AtualizarServicoRequestDTO requestDTO) {
        ServicoProteticoDTO updated = servicoProteticoService
                .atualizarServico(proteticoId, tipoServico, requestDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Alterar status ativo/inativo de um serviço
     * PATCH /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}/status
     */
    @PatchMapping("/protetico/{proteticoId}/tipo/{tipoServico}/status")
    public ResponseEntity<ServicoProteticoDTO> alterarStatusServico(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico,
            @RequestParam boolean ativo) {
        ServicoProteticoDTO updated = servicoProteticoService
                .alterarStatusServico(proteticoId, tipoServico, ativo);
        return ResponseEntity.ok(updated);
    }

    /**
     * Remover serviço
     * DELETE /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}
     */
    @DeleteMapping("/protetico/{proteticoId}/tipo/{tipoServico}")
    public ResponseEntity<Void> removerServico(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico) {
        servicoProteticoService.removerServico(proteticoId, tipoServico);
        return ResponseEntity.noContent().build();
    }

    /**
     * Buscar protéticos que oferecem um tipo de serviço
     * GET /api/servicos-protetico/tipo/{tipoServico}/proteticos
     */
    @GetMapping("/tipo/{tipoServico}/proteticos")
    public ResponseEntity<List<ServicoProteticoDTO>> buscarProteticosPorServico(
            @PathVariable TipoServico tipoServico) {
        List<ServicoProteticoDTO> proteticos = servicoProteticoService
                .buscarProteticosPorServico(tipoServico);
        return ResponseEntity.ok(proteticos);
    }

    // ============ ENDPOINTS ESPECÍFICOS PARA TERCEIRIZAÇÃO ============

    /**
     * Buscar serviços que aceitam terceirização de um tipo específico
     * GET /api/servicos-protetico/tipo/{tipoServico}/aceitam-terceirizacao
     */
    @GetMapping("/tipo/{tipoServico}/aceitam-terceirizacao")
    public ResponseEntity<List<ServicoProteticoDTO>> buscarServicosAceitamTerceirizacao(
            @PathVariable TipoServico tipoServico) {
        List<ServicoProteticoDTO> servicos = servicoProteticoService
                .buscarServicosAceitamTerceirizacao(tipoServico);
        return ResponseEntity.ok(servicos);
    }

    /**
     * Buscar serviços que um protético pode terceirizar
     * GET /api/servicos-protetico/protetico/{proteticoId}/pode-terceirizar
     */
    @GetMapping("/protetico/{proteticoId}/pode-terceirizar")
    public ResponseEntity<List<ServicoProteticoDTO>> buscarServicosQuePossoTerceirizar(
            @PathVariable Long proteticoId) {
        List<ServicoProteticoDTO> servicos = servicoProteticoService
                .buscarServicosQuePossoTerceirizar(proteticoId);
        return ResponseEntity.ok(servicos);
    }

    /**
     * Verificar se um protético pode terceirizar um serviço específico
     * GET /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}/pode-terceirizar
     */
    @GetMapping("/protetico/{proteticoId}/tipo/{tipoServico}/pode-terceirizar")
    public ResponseEntity<Boolean> podeTerceirizarServico(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico) {
        boolean pode = servicoProteticoService.podeTerceirizarServico(proteticoId, tipoServico);
        return ResponseEntity.ok(pode);
    }

    /**
     * Buscar sugestão de terceirizado para um serviço
     * GET /api/servicos-protetico/protetico/{proteticoId}/tipo/{tipoServico}/sugerir-terceirizado
     */
    @GetMapping("/protetico/{proteticoId}/tipo/{tipoServico}/sugerir-terceirizado")
    public ResponseEntity<ServicoProteticoDTO> sugerirTerceirizado(
            @PathVariable Long proteticoId,
            @PathVariable TipoServico tipoServico) {
        return servicoProteticoService
                .sugerirTerceirizado(proteticoId, tipoServico)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}