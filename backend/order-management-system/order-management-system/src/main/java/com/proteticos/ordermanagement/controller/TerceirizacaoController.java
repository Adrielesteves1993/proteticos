package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.model.Terceirizacao;
import com.proteticos.ordermanagement.service.TerceirizacaoService;
import com.proteticos.ordermanagement.DTO.SolicitacaoTerceirizacaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/terceirizacoes")
public class TerceirizacaoController {

    @Autowired
    private TerceirizacaoService terceirizacaoService;

    @PostMapping("/solicitar")
    public ResponseEntity<Terceirizacao> solicitarTerceirizacao(@RequestBody SolicitacaoTerceirizacaoDTO solicitacaoDTO) {
        try {
            Terceirizacao terceirizacao = terceirizacaoService.solicitarTerceirizacao(solicitacaoDTO);
            return ResponseEntity.ok(terceirizacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/aceitar")
    public ResponseEntity<Terceirizacao> aceitarTerceirizacao(
            @PathVariable Long id,
            @RequestParam Long proteticoDestinoId) {
        try {
            Terceirizacao terceirizacao = terceirizacaoService.aceitarSolicitacao(id, proteticoDestinoId);
            return ResponseEntity.ok(terceirizacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}/recusar")
    public ResponseEntity<Terceirizacao> recusarTerceirizacao(
            @PathVariable Long id,
            @RequestParam Long proteticoDestinoId) {
        try {
            Terceirizacao terceirizacao = terceirizacaoService.recusarSolicitacao(id, proteticoDestinoId);
            return ResponseEntity.ok(terceirizacao);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/protetico/{proteticoId}/solicitacoes-recebidas")
    public List<Terceirizacao> getSolicitacoesRecebidas(@PathVariable Long proteticoId) {
        return terceirizacaoService.getSolicitacoesRecebidas(proteticoId);
    }

    @GetMapping("/protetico/{proteticoId}/solicitacoes-enviadas")
    public List<Terceirizacao> getSolicitacoesEnviadas(@PathVariable Long proteticoId) {
        return terceirizacaoService.getSolicitacoesEnviadas(proteticoId);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Terceirizacao> getTerceirizacoesPorPedido(@PathVariable Long pedidoId) {
        return terceirizacaoService.getTerceirizacoesPorPedido(pedidoId);
    }
}