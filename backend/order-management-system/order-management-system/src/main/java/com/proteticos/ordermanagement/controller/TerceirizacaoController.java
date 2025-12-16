package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.DTO.*;
import com.proteticos.ordermanagement.model.TipoServico;
import com.proteticos.ordermanagement.service.TerceirizacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/terceirizacoes")
public class TerceirizacaoController {

    @Autowired
    private TerceirizacaoService terceirizacaoService;

    // ============ SOLICITAÇÃO DE TERCEIRIZAÇÃO ============

    /**
     * Solicita terceirização de um pedido
     * POST /api/terceirizacoes/pedido/{pedidoId}/solicitar
     */
    @PostMapping("/pedido/{pedidoId}/solicitar")
    public ResponseEntity<?> solicitarTerceirizacao(
            @PathVariable Long pedidoId,
            @RequestParam Long proteticoId, // ID do protético que está solicitando
            @RequestBody SolicitarTerceirizacaoRequest request) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.solicitarTerceirizacao(
                    pedidoId, proteticoId, request
            );

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Terceirização solicitada com sucesso!");
            successResponse.put("data", response);

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ============ ACEITAR/RECUSAR TERCEIRIZAÇÃO ============

    /**
     * Aceita uma terceirização (protético terceirizado)
     * PUT /api/terceirizacoes/pedido/{pedidoId}/aceitar
     */
    @PutMapping("/pedido/{pedidoId}/aceitar")
    public ResponseEntity<?> aceitarTerceirizacao(
            @PathVariable Long pedidoId,
            @RequestParam Long proteticoId) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.aceitarTerceirizacao(pedidoId, proteticoId);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Terceirização aceita com sucesso!");
            successResponse.put("data", response);

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Recusa uma terceirização (protético terceirizado)
     * PUT /api/terceirizacoes/pedido/{pedidoId}/recusar
     */
    @PutMapping("/pedido/{pedidoId}/recusar")
    public ResponseEntity<?> recusarTerceirizacao(
            @PathVariable Long pedidoId,
            @RequestParam Long proteticoId,
            @RequestParam(required = false) String motivo) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.recusarTerceirizacao(pedidoId, proteticoId, motivo);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Terceirização recusada com sucesso!");
            successResponse.put("data", response);

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ============ EXECUÇÃO DA TERCEIRIZAÇÃO ============

    /**
     * Inicia a execução da terceirização (protético terceirizado)
     * PUT /api/terceirizacoes/pedido/{pedidoId}/iniciar
     */
    @PutMapping("/pedido/{pedidoId}/iniciar")
    public ResponseEntity<?> iniciarTerceirizacao(
            @PathVariable Long pedidoId,
            @RequestParam Long proteticoId) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.iniciarTerceirizacao(pedidoId, proteticoId);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Execução da terceirização iniciada!");
            successResponse.put("data", response);

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Conclui a terceirização (protético terceirizado)
     * PUT /api/terceirizacoes/pedido/{pedidoId}/concluir
     */
    @PutMapping("/pedido/{pedidoId}/concluir")
    public ResponseEntity<?> concluirTerceirizacao(
            @PathVariable Long pedidoId,
            @RequestParam Long proteticoId) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.concluirTerceirizacao(pedidoId, proteticoId);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Terceirização concluída com sucesso!");
            successResponse.put("data", response);

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Cancela uma terceirização (qualquer protético envolvido)
     * PUT /api/terceirizacoes/pedido/{pedidoId}/cancelar
     */
    @PutMapping("/pedido/{pedidoId}/cancelar")
    public ResponseEntity<?> cancelarTerceirizacao(
            @PathVariable Long pedidoId,
            @RequestParam Long proteticoId,
            @RequestParam(required = false) String motivo) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.cancelarTerceirizacao(pedidoId, proteticoId, motivo);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("success", true);
            successResponse.put("message", "Terceirização cancelada com sucesso!");
            successResponse.put("data", response);

            return ResponseEntity.ok(successResponse);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ============ CONSULTAS ============

    /**
     * Busca terceirização por ID do pedido
     * GET /api/terceirizacoes/pedido/{pedidoId}
     */
    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> buscarTerceirizacaoPorPedido(@PathVariable Long pedidoId) {
        try {
            TerceirizacaoResponseDTO response = terceirizacaoService.buscarPorPedidoId(pedidoId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lista todas terceirizações de um protético
     * GET /api/terceirizacoes/protetico/{proteticoId}
     */
    @GetMapping("/protetico/{proteticoId}")
    public ResponseEntity<?> listarTerceirizacoesPorProtetico(@PathVariable Long proteticoId) {
        try {
            List<TerceirizacaoResponseDTO> response = terceirizacaoService.listarTerceirizacoesPorProtetico(proteticoId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Lista protéticos disponíveis para terceirização
     * GET /api/terceirizacoes/disponiveis?pedidoId=1&tipoServico=COROA
     */
    @GetMapping("/disponiveis")
    public ResponseEntity<?> listarProteticosDisponiveis(
            @RequestParam(required = false) Long pedidoId,
            @RequestParam(required = false) String tipoServico) {

        try {
            if (pedidoId == null && tipoServico == null) {
                throw new RuntimeException("Informe pedidoId ou tipoServico");
            }

            List<ProteticoSimplesDTO> response;

            if (tipoServico != null) {
                // Usa o NOVO método que aceita String
                response = terceirizacaoService.buscarProteticosSimplesPorServico(tipoServico);
            } else if (pedidoId != null) {
                // Para pedidoId, ainda usa o método antigo (precisa do tipoServico do pedido)
                throw new RuntimeException("Informe tipoServico ou implemente busca do pedido");
            } else {
                throw new RuntimeException("Parâmetros inválidos");
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ============ NOVO ENDPOINT ADICIONADO ============

    /**
     * NOVO ENDPOINT: Lista protéticos que oferecem um tipo específico de serviço
     * GET /api/terceirizacoes/disponiveis-por-servico?tipoServico=protese_total&excluirProteticoId=1
     *
     * Para terceirização: mostra quem pode executar este serviço
     */
    @GetMapping("/disponiveis-por-servico")
    public ResponseEntity<?> listarProteticosPorServico(
            @RequestParam String tipoServico,
            @RequestParam(required = false) Long excluirProteticoId) {

        System.out.println("=== ENDPOINT: /disponiveis-por-servico ===");
        System.out.println("tipoServico: " + tipoServico);
        System.out.println("excluirProteticoId: " + excluirProteticoId);

        try {
            if (tipoServico == null || tipoServico.trim().isEmpty()) {
                throw new RuntimeException("Informe o tipoServico");
            }

            // Chama o método NOVO do service
            List<ProteticoSimplesDTO> proteticos = terceirizacaoService
                    .listarProteticosPorServicoAtivos(tipoServico, excluirProteticoId);

            System.out.println("✅ Retornando " + proteticos.size() + " protéticos");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", proteticos);
            response.put("total", proteticos.size());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.out.println("❌ Erro: " + e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}