package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.model.EtapaPedido;
import com.proteticos.ordermanagement.service.EtapaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etapas")
public class EtapaPedidoController {

    @Autowired
    private EtapaService etapaService;

    // Buscar todas etapas de um pedido
    @GetMapping("/pedido/{pedidoId}")
    public List<EtapaPedido> buscarEtapasPorPedido(@PathVariable Long pedidoId) {
        return etapaService.buscarEtapasPorPedido(pedidoId);
    }

    // Buscar etapa atual do pedido
    @GetMapping("/pedido/{pedidoId}/atual")
    public ResponseEntity<EtapaPedido> buscarEtapaAtual(@PathVariable Long pedidoId) {
        EtapaPedido etapaAtual = etapaService.buscarEtapaAtual(pedidoId);
        return etapaAtual != null ?
                ResponseEntity.ok(etapaAtual) :
                ResponseEntity.notFound().build();
    }

    // Concluir etapa e avançar automaticamente para a próxima
    @PostMapping("/{etapaId}/concluir")
    public ResponseEntity<EtapaPedido> concluirEtapa(
            @PathVariable Long etapaId,
            @RequestParam Long proteticoId) {
        try {
            EtapaPedido etapaConcluida = etapaService.concluirEtapa(etapaId, proteticoId);
            return ResponseEntity.ok(etapaConcluida);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}