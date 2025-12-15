package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.DTO.ProteticoDTO;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.service.ProteticoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proteticos")
public class ProteticoController {

    @Autowired
    private ProteticoService proteticoService;

    // ============ LISTAGEM ============

    /**
     * Lista todos os protéticos
     * GET /api/proteticos
     */
    @GetMapping
    public ResponseEntity<List<ProteticoDTO>> listarTodos() {
        List<ProteticoDTO> proteticos = proteticoService.listarTodos();
        return ResponseEntity.ok(proteticos);
    }

    /**
     * Busca protético por ID
     * GET /api/proteticos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProteticoDTO> buscarPorId(@PathVariable Long id) {
        ProteticoDTO protetico = proteticoService.buscarPorId(id);
        if (protetico == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(protetico);
    }

    // ============ CRIAÇÃO ============

    /**
     * Cria um novo protético
     * POST /api/proteticos
     */
    @PostMapping
    public ResponseEntity<?> criarProtetico(@RequestBody Protetico protetico) {
        try {
            ProteticoDTO novo = proteticoService.criarProtetico(protetico);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(novo);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // ============ ATUALIZAÇÃO ============

    /**
     * Atualiza um protético existente
     * PUT /api/proteticos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProtetico(
            @PathVariable Long id,
            @RequestBody ProteticoDTO proteticoDTO) {
        try {
            ProteticoDTO atualizado = proteticoService.atualizarProtetico(id, proteticoDTO);
            return ResponseEntity.ok(atualizado);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // ============ EXCLUSÃO ============

    /**
     * Exclui um protético
     * DELETE /api/proteticos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirProtetico(@PathVariable Long id) {
        try {
            proteticoService.excluirProtetico(id);
            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // ============ BUSCAS ESPECÍFICAS ============

    /**
     * Busca protéticos por especialização
     * GET /api/proteticos/buscar?especializacao=Zircônia
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorEspecializacao(
            @RequestParam String especializacao) {
        try {
            List<ProteticoDTO> proteticos = proteticoService.buscarPorEspecializacao(especializacao);
            return ResponseEntity.ok(proteticos);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Busca protéticos por nome
     * GET /api/proteticos/buscar/nome?nome=Carlos
     */
    @GetMapping("/buscar/nome")
    public ResponseEntity<?> buscarPorNome(@RequestParam String nome) {
        try {
            List<ProteticoDTO> proteticos = proteticoService.buscarPorNome(nome);
            return ResponseEntity.ok(proteticos);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Busca protéticos que aceitam terceirização
     * GET /api/proteticos/aceitam-terceirizacao
     */
    @GetMapping("/aceitam-terceirizacao")
    public ResponseEntity<?> listarQueAceitamTerceirizacao() {
        try {
            List<ProteticoDTO> proteticos = proteticoService.listarTodos()
                    .stream()
                    .filter(ProteticoDTO::isAceitaTerceirizacao)
                    .toList();
            return ResponseEntity.ok(proteticos);

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    // ============ MÉTODOS AUXILIARES ============

    /**
     * Cria resposta de erro padronizada
     */
    private Object createErrorResponse(String message) {
        return new Object() {
            public final boolean success = false;
            public final String error = message;
        };
    }
}