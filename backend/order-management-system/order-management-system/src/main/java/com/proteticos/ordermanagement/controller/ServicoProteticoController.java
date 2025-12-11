package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.DTO.ServicoProteticoDTO;
import com.proteticos.ordermanagement.DTO.ServicoProteticoRequestDTO;
import com.proteticos.ordermanagement.DTO.AtualizarServicoRequestDTO; // NOVO DTO
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.service.ServicoProteticoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/proteticos/{proteticoId}/servicos")
public class ServicoProteticoController {

    @Autowired
    private ServicoProteticoService servicoProteticoService;

    @GetMapping
    public ResponseEntity<List<ServicoProteticoDTO>> listarServicos(@PathVariable Long proteticoId) {
        List<ServicoProteticoDTO> servicos = servicoProteticoService.listarServicosPorProtetico(proteticoId);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<ServicoProteticoDTO>> listarServicosAtivos(@PathVariable Long proteticoId) {
        List<ServicoProteticoDTO> servicos = servicoProteticoService.listarServicosAtivosPorProtetico(proteticoId);
        return ResponseEntity.ok(servicos);
    }

    @GetMapping("/{tipoServico}")
    public ResponseEntity<ServicoProteticoDTO> buscarServico(
            @PathVariable Long proteticoId,
            @PathVariable String tipoServico) {

        TipoServico tipo = TipoServico.fromValue(tipoServico);
        if (tipo == null) {
            return ResponseEntity.badRequest().build();
        }

        ServicoProteticoDTO servico = servicoProteticoService.buscarServicoPorTipo(proteticoId, tipo);
        if (servico == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(servico);
    }

    @PostMapping
    public ResponseEntity<ServicoProteticoDTO> adicionarServico(
            @PathVariable Long proteticoId,
            @Valid @RequestBody ServicoProteticoRequestDTO servicoDTO) {

        try {
            ServicoProteticoDTO servico = servicoProteticoService
                    .adicionarOuAtualizarServico(proteticoId, servicoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // NOVO ENDPOINT para atualizar serviço completo
    @PutMapping("/{tipoServico}")
    public ResponseEntity<ServicoProteticoDTO> atualizarServico(
            @PathVariable Long proteticoId,
            @PathVariable String tipoServico,
            @Valid @RequestBody AtualizarServicoRequestDTO requestDTO) {

        TipoServico tipo = TipoServico.fromValue(tipoServico);
        if (tipo == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ServicoProteticoDTO servico = servicoProteticoService
                    .atualizarServico(proteticoId, tipo, requestDTO);
            return ResponseEntity.ok(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{tipoServico}/preco")
    public ResponseEntity<ServicoProteticoDTO> atualizarPreco(
            @PathVariable Long proteticoId,
            @PathVariable String tipoServico,
            @RequestBody BigDecimal novoPreco) {

        TipoServico tipo = TipoServico.fromValue(tipoServico);
        if (tipo == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ServicoProteticoDTO servico = servicoProteticoService
                    .atualizarPrecoServico(proteticoId, tipo, novoPreco);
            return ResponseEntity.ok(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{tipoServico}/status")
    public ResponseEntity<ServicoProteticoDTO> alterarStatus(
            @PathVariable Long proteticoId,
            @PathVariable String tipoServico,
            @RequestBody boolean ativo) {

        TipoServico tipo = TipoServico.fromValue(tipoServico);
        if (tipo == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ServicoProteticoDTO servico = servicoProteticoService
                    .alterarStatusServico(proteticoId, tipo, ativo);
            return ResponseEntity.ok(servico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{tipoServico}")
    public ResponseEntity<Void> removerServico(
            @PathVariable Long proteticoId,
            @PathVariable String tipoServico) {

        TipoServico tipo = TipoServico.fromValue(tipoServico);
        if (tipo == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            servicoProteticoService.removerServico(proteticoId, tipo);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar/{tipoServico}")
    public ResponseEntity<List<ServicoProteticoDTO>> buscarProteticosPorServico(
            @PathVariable String tipoServico) {

        TipoServico tipo = TipoServico.fromValue(tipoServico);
        if (tipo == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ServicoProteticoDTO> proteticos = servicoProteticoService
                .buscarProteticosPorServico(tipo);
        return ResponseEntity.ok(proteticos);
    }
}