package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.DTO.CriarPedidoRequest;
import com.proteticos.ordermanagement.DTO.PedidoResponseDTO;
import com.proteticos.ordermanagement.model.Pedido;
import com.proteticos.ordermanagement.model.StatusPedido;
import com.proteticos.ordermanagement.model.Dentista;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.repository.DentistaRepository;
import com.proteticos.ordermanagement.repository.PedidoRepository;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import com.proteticos.ordermanagement.service.EtapaService;
import com.proteticos.ordermanagement.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "http://localhost:3000")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    @Autowired
    private EtapaService etapaService;

    @Autowired
    private PedidoService pedidoService;

    // ============ MÉTODOS DE TESTE SIMPLES (mantidos como estão) ============
    // ... (métodos ping, count-simple, ids, limit, debug-simple permanecem iguais) ...

    // ============ MÉTODOS PRINCIPAIS ATUALIZADOS ============

    // Listar todos os pedidos - RETORNA DTO
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    // TESTE DIRETO - RETORNA DTO
    @GetMapping("/teste/{id}")
    public ResponseEntity<PedidoResponseDTO> testePedido(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        return pedidoOpt.map(pedido -> ResponseEntity.ok(pedidoService.converterParaDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar pedido por ID - RETORNA DTO
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        return pedidoOpt.map(pedido -> ResponseEntity.ok(pedidoService.converterParaDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar pedido por código - RETORNA DTO
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PedidoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findByCodigo(codigo);
        return pedidoOpt.map(pedido -> ResponseEntity.ok(pedidoService.converterParaDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ============ CRIAÇÃO DE PEDIDOS ============

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        if (!dentistaRepository.existsById(pedido.getDentista().getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!proteticoRepository.existsById(pedido.getProtetico().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Pedido salvo = pedidoRepository.save(pedido);
        etapaService.criarEtapasPadrao(salvo);
        return ResponseEntity.ok(salvo);
    }

    // ✅ NOVO MÉTODO SIMPLIFICADO DE CRIAÇÃO
    @PostMapping("/novo")
    public ResponseEntity<?> criarPedidoNovo(@RequestBody CriarPedidoRequest request) {
        try {
            // Usa o PedidoService para criar (já tem validação e lógica)
            Pedido pedidoCriado = pedidoService.criarPedido(request);

            return ResponseEntity.ok(pedidoCriado);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ============ MÉTODOS DE STATUS E ATUALIZAÇÃO (ATUALIZADOS) ============

    // ✅ MÉTODO ATUALIZADO - Agora valida transições usando o PedidoService
    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            System.out.println("=== ATUALIZANDO STATUS ===");
            System.out.println("Pedido ID: " + id + " | Status recebido: '" + status + "'");

            // Converte string para enum
            StatusPedido novoStatus;
            try {
                novoStatus = StatusPedido.valueOf(status.toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Status inválido: '" + status + "'");
                error.put("valid_values", Arrays.toString(StatusPedido.values()));
                return ResponseEntity.badRequest().body(error);
            }

            // Usa o PedidoService para atualizar (já tem validação)
            Pedido pedidoAtualizado = pedidoService.atualizarStatus(id, novoStatus);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status atualizado com sucesso");
            response.put("pedido", pedidoService.converterParaDTO(pedidoAtualizado));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ NOVO MÉTODO: Aprovar pedido (usa método específico do service)
    @PutMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarPedido(@PathVariable Long id) {
        try {
            Pedido pedidoAprovado = pedidoService.aprovarPedido(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido aprovado com sucesso!");
            response.put("pedido", pedidoService.converterParaDTO(pedidoAprovado));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erro interno ao aprovar pedido");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ NOVO MÉTODO: Iniciar produção
    @PutMapping("/{id}/iniciar-producao")
    public ResponseEntity<?> iniciarProducao(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.iniciarProducao(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produção iniciada com sucesso!");
            response.put("pedido", pedidoService.converterParaDTO(pedido));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ NOVO MÉTODO: Finalizar pedido (usando método específico)
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.finalizarPedido(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido finalizado com sucesso!");
            response.put("pedido", pedidoService.converterParaDTO(pedido));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ NOVO MÉTODO: Cancelar pedido
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarPedido(@PathVariable Long id) {
        try {
            Pedido pedido = pedidoService.cancelarPedido(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido cancelado com sucesso!");
            response.put("pedido", pedidoService.converterParaDTO(pedido));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ✅ MÉTODO: Obter próximos status possíveis
    @GetMapping("/{id}/proximos-status")
    public ResponseEntity<?> getProximosStatus(@PathVariable Long id) {
        try {
            List<StatusPedido> proximos = pedidoService.getProximosStatusPossiveis(id);

            Map<String, Object> response = new HashMap<>();
            response.put("pedidoId", id);
            response.put("proximosStatus", proximos.stream()
                    .map(StatusPedido::name)
                    .collect(Collectors.toList()));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ============ MÉTODOS DE ATUALIZAÇÃO GERAL ============

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();

            // Só permite editar se não estiver finalizado ou cancelado
            if (pedido.getStatus().isEstadoFinal()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Não é possível editar um pedido finalizado ou cancelado");
                return ResponseEntity.badRequest().body(error);
            }

            // Atualiza valor
            if (updates.containsKey("valorCobrado")) {
                Object valorObj = updates.get("valorCobrado");
                if (valorObj instanceof Number) {
                    BigDecimal valor = BigDecimal.valueOf(((Number) valorObj).doubleValue());
                    pedido.setValorCobrado(valor);
                }
            }

            // Atualiza data prevista
            if (updates.containsKey("dataPrevistaEntrega")) {
                String dataStr = updates.get("dataPrevistaEntrega").toString();
                try {
                    pedido.setDataPrevistaEntrega(LocalDate.parse(dataStr));
                } catch (Exception e) {
                    // Ignora data inválida
                }
            }

            // Atualiza informações detalhadas
            if (updates.containsKey("informacoesDetalhadas")) {
                pedido.setInformacoesDetalhadas(updates.get("informacoesDetalhadas").toString());
            }

            Pedido pedidoSalvo = pedidoRepository.save(pedido);
            return ResponseEntity.ok(pedidoSalvo);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ============ MÉTODOS DE BUSCA POR RELACIONAMENTO ============

    @GetMapping("/dentista/{dentistaId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorDentista(@PathVariable Long dentistaId) {
        List<Pedido> pedidos = pedidoRepository.findByDentistaId(dentistaId);
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/protetico/{proteticoId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorProtetico(@PathVariable Long proteticoId) {
        List<Pedido> pedidos = pedidoRepository.findByProteticoId(proteticoId);
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = pedidoRepository.findByStatus(status);
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    // ============ MÉTODOS ESPECIAIS PARA FRONTEND ============
    // ... (métodos para-frontend e para-frontend-protetico permanecem iguais) ...
}