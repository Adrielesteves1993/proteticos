package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.dto.CriarPedidoRequest;
import com.proteticos.ordermanagement.dto.PedidoResponseDTO;
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

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "Pedido controller está funcionando");
        response.put("time", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count-simple")
    public ResponseEntity<String> countSimple() {
        try {
            long count = pedidoRepository.count();
            return ResponseEntity.ok("Total de pedidos: " + count);
        } catch (Exception e) {
            return ResponseEntity.ok("Erro ao contar: " + e.getMessage());
        }
    }

    @GetMapping("/ids")
    public ResponseEntity<List<Long>> listarIds() {
        try {
            List<Pedido> todos = pedidoRepository.findAll();
            List<Long> ids = new ArrayList<>();

            for (Pedido p : todos) {
                ids.add(p.getId());
            }

            System.out.println("IDs encontrados: " + ids);
            return ResponseEntity.ok(ids);

        } catch (Exception e) {
            System.err.println("Erro ao buscar IDs: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/limit/{limit}")
    public ResponseEntity<?> listarComLimite(@PathVariable int limit) {
        try {
            System.out.println("=== LISTANDO " + limit + " PEDIDOS ===");

            List<Pedido> todosPedidos = pedidoRepository.findAll();
            System.out.println("Total no banco: " + todosPedidos.size());

            // Limita a quantidade manualmente
            List<Pedido> pedidosLimitados = new ArrayList<>();
            for (int i = 0; i < Math.min(limit, todosPedidos.size()); i++) {
                pedidosLimitados.add(todosPedidos.get(i));
            }

            System.out.println("Retornando: " + pedidosLimitados.size() + " pedidos");

            // IMPORTANTE: Desativa etapas para evitar recursão
            for (Pedido p : pedidosLimitados) {
                p.setEtapas(null);
            }

            return ResponseEntity.ok(pedidosLimitados);

        } catch (Exception e) {
            System.err.println("ERRO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/debug-simple")
    public ResponseEntity<Map<String, Object>> debugSimple() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Conta pedidos
            long count = pedidoRepository.count();
            response.put("total_pedidos", count);

            // Tenta buscar 1 pedido
            List<Pedido> pedidos = pedidoRepository.findAll();
            if (!pedidos.isEmpty()) {
                Pedido primeiro = pedidos.get(0);
                Map<String, Object> pedidoInfo = new HashMap<>();
                pedidoInfo.put("id", primeiro.getId());
                pedidoInfo.put("codigo", primeiro.getCodigo());
                pedidoInfo.put("status", primeiro.getStatus().toString());

                if (primeiro.getDentista() != null) {
                    pedidoInfo.put("dentista_nome", primeiro.getDentista().getNome());
                    pedidoInfo.put("dentista_id", primeiro.getDentista().getId());
                }
                if (primeiro.getProtetico() != null) {
                    pedidoInfo.put("protetico_nome", primeiro.getProtetico().getNome());
                    pedidoInfo.put("protetico_id", primeiro.getProtetico().getId());
                }

                response.put("primeiro_pedido", pedidoInfo);
            }

            response.put("status", "success");
            response.put("timestamp", LocalDateTime.now().toString());

        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("error_type", e.getClass().getName());
        }

        return ResponseEntity.ok(response);
    }

    // ============ MÉTODOS PRINCIPAIS ATUALIZADOS (agora retornam DTOs) ============

    // Listar todos os pedidos - AGORA RETORNA DTO
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        List<Pedido> pedidos = pedidoRepository.findAll();
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    // TESTE DIRETO - AGORA RETORNA DTO
    @GetMapping("/teste/{id}")
    public ResponseEntity<PedidoResponseDTO> testePedido(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isPresent()) {
            PedidoResponseDTO dto = pedidoService.converterParaDTO(pedidoOpt.get());
            return ResponseEntity.ok(dto);
        }

        return ResponseEntity.notFound().build();
    }

    // Buscar pedido por ID - AGORA RETORNA DTO
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
        return pedidoOpt.map(pedido -> ResponseEntity.ok(pedidoService.converterParaDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Buscar pedido por código - AGORA RETORNA DTO
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PedidoResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findByCodigo(codigo);
        return pedidoOpt.map(pedido -> ResponseEntity.ok(pedidoService.converterParaDTO(pedido)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Criar novo pedido - MANTIDO (retorna Pedido mesmo, pode mudar depois)
    @PostMapping
    public ResponseEntity<Pedido> criarPedido(@RequestBody Pedido pedido) {
        if (!dentistaRepository.existsById(pedido.getDentista().getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!proteticoRepository.existsById(pedido.getProtetico().getId())) {
            return ResponseEntity.badRequest().build();
        }

        Pedido salvo = pedidoRepository.save(pedido);

        // CRIAR ETAPAS PADRÃO AUTOMATICAMENTE
        etapaService.criarEtapasPadrao(salvo);

        return ResponseEntity.ok(salvo);
    }

    // ✅✅✅ MÉTODO NOVO CORRIGIDO (mantido)
    @PostMapping("/novo")
    public ResponseEntity<?> criarPedidoSimples(@RequestBody CriarPedidoRequest request) {
        try {
            System.out.println("=== RECEBENDO PEDIDO ===");
            System.out.println("Dentista ID: " + request.getDentistaId());
            System.out.println("Protético ID: " + request.getProteticoId());
            System.out.println("Tipo Serviço: " + request.getTipoServico());

            // Buscar dentista
            Optional<Dentista> dentistaOpt = dentistaRepository.findById(request.getDentistaId());
            if (dentistaOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Dentista não encontrado");
            }

            // Buscar protético
            Optional<Protetico> proteticoOpt = proteticoRepository.findById(request.getProteticoId());
            if (proteticoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Protético não encontrado");
            }

            // Criar pedido
            Pedido pedido = new Pedido();
            pedido.setDentista(dentistaOpt.get());
            pedido.setProtetico(proteticoOpt.get());
            pedido.setTipoServico(request.getTipoServico());
            pedido.setInformacoesDetalhadas(request.getInformacoesDetalhadas());
            pedido.setDataPrevistaEntrega(request.getDataPrevistaEntrega());
            pedido.setValorCobrado(request.getValorCobrado());

            // Data de entrada - usar do request ou data atual
            if (request.getDataEntrada() != null) {
                pedido.setDataEntrada(request.getDataEntrada());
            }

            Pedido salvo = pedidoRepository.save(pedido);

            // Criar etapas padrão se solicitado
            if (request.isCriarEtapasIniciais()) {
                etapaService.criarEtapasPadrao(salvo);
            }

            System.out.println("=== PEDIDO CRIADO COM SUCESSO ===");
            System.out.println("ID: " + salvo.getId());
            System.out.println("Código: " + salvo.getCodigo());

            return ResponseEntity.ok(salvo);

        } catch (Exception e) {
            System.out.println("=== ERRO AO CRIAR PEDIDO ===");
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    // ============ MÉTODOS DE STATUS E ATUALIZAÇÃO (mantidos) ============

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            System.out.println("=== ATUALIZANDO STATUS (VERSÃO SIMPLES) ===");
            System.out.println("Pedido ID: " + id);
            System.out.println("Status recebido: '" + status + "'");

            // 1. Busca o pedido
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (pedidoOpt.isEmpty()) {
                System.out.println("❌ Pedido não encontrado: " + id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Pedido não encontrado");
                return ResponseEntity.status(404).body(error);
            }

            Pedido pedido = pedidoOpt.get();
            System.out.println("Status atual: " + pedido.getStatus());
            System.out.println("Código: " + pedido.getCodigo());

            // 2. Converte o status para enum
            StatusPedido novoStatus;
            try {
                String statusClean = status.toUpperCase().trim();
                novoStatus = StatusPedido.valueOf(statusClean);
                System.out.println("✅ Status convertido: " + novoStatus);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Erro na conversão do status: " + e.getMessage());
                System.out.println("✅ Valores válidos do enum:");
                for (StatusPedido s : StatusPedido.values()) {
                    System.out.println("  - " + s.name());
                }

                Map<String, Object> error = new HashMap<>();
                error.put("error", "Status inválido: '" + status + "'");
                error.put("valid_values", Arrays.toString(StatusPedido.values()));
                return ResponseEntity.badRequest().body(error);
            }

            // 3. Atualiza o pedido
            pedido.setStatus(novoStatus);

            // 4. Se for um status final, define data de entrega
            if (novoStatus == StatusPedido.ENTREGUE ||
                    novoStatus == StatusPedido.FINALIZADO ||
                    novoStatus == StatusPedido.CONCLUIDO) {
                pedido.setDataEntrega(LocalDate.now());
                System.out.println("📅 Data de entrega definida: " + LocalDate.now());
            }

            // 5. Salva
            Pedido pedidoSalvo = pedidoRepository.save(pedido);
            System.out.println("✅ Pedido salvo com sucesso");

            // 6. Retorna resposta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status atualizado com sucesso");
            response.put("pedido", Map.of(
                    "id", pedidoSalvo.getId(),
                    "codigo", pedidoSalvo.getCodigo(),
                    "status", pedidoSalvo.getStatus().toString(),
                    "status_anterior", pedido.getStatus().toString()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ ERRO INESPERADO: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<?> aprovarPedido(@PathVariable Long id) {
        try {
            System.out.println("=== APROVANDO PEDIDO NO CONTROLLER ===");
            System.out.println("Pedido ID: " + id);

            // Chama o service para aprovar o pedido
            Pedido pedidoAprovado = pedidoService.aprovarPedido(id);

            // Cria resposta para o frontend
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido aprovado com sucesso!");
            response.put("pedido", Map.of(
                    "id", pedidoAprovado.getId(),
                    "codigo", pedidoAprovado.getCodigo(),
                    "status", pedidoAprovado.getStatus().toString(),
                    "tipoServico", pedidoAprovado.getTipoServico().toString()
            ));

            System.out.println("✅ Resposta do controller: " + response);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            System.err.println("❌ Erro ao aprovar pedido: " + e.getMessage());

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());

            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            System.err.println("💥 Erro inesperado ao aprovar pedido: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erro interno ao aprovar pedido");

            return ResponseEntity.internalServerError().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPedido(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            System.out.println("=== ATUALIZANDO PEDIDO " + id + " ===");
            System.out.println("Updates recebidos: " + updates);

            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();

            // Atualiza valor
            if (updates.containsKey("valorCobrado")) {
                Object valorObj = updates.get("valorCobrado");
                if (valorObj instanceof Number) {
                    BigDecimal valor = BigDecimal.valueOf(((Number) valorObj).doubleValue());
                    pedido.setValorCobrado(valor);
                    System.out.println("💰 Valor atualizado para: " + valor);
                }
            }

            // Atualiza data prevista
            if (updates.containsKey("dataPrevistaEntrega")) {
                String dataStr = updates.get("dataPrevistaEntrega").toString();
                try {
                    LocalDate data = LocalDate.parse(dataStr);
                    pedido.setDataPrevistaEntrega(data);
                    System.out.println("📅 Data prevista atualizada para: " + data);
                } catch (Exception e) {
                    System.out.println("⚠️ Data inválida: " + dataStr);
                }
            }

            // Atualiza informações detalhadas
            if (updates.containsKey("informacoesDetalhadas")) {
                String info = updates.get("informacoesDetalhadas").toString();
                pedido.setInformacoesDetalhadas(info);
                System.out.println("📝 Informações atualizadas");
            }

            // Salva
            Pedido pedidoSalvo = pedidoRepository.save(pedido);
            System.out.println("✅ Pedido atualizado com sucesso!");

            return ResponseEntity.ok(pedidoSalvo);

        } catch (Exception e) {
            System.err.println("❌ Erro ao atualizar pedido: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarPedido(@PathVariable Long id) {
        try {
            System.out.println("=== FINALIZANDO PEDIDO ===");
            System.out.println("Pedido ID: " + id);

            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (pedidoOpt.isEmpty()) {
                System.out.println("❌ Pedido não encontrado: " + id);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Pedido não encontrado");
                return ResponseEntity.status(404).body(error);
            }

            Pedido pedido = pedidoOpt.get();
            System.out.println("Status atual: " + pedido.getStatus());

            // Verifica se pode ser finalizado
            if (pedido.getStatus() == StatusPedido.ENTREGUE ||
                    pedido.getStatus() == StatusPedido.CANCELADO ||
                    pedido.getStatus() == StatusPedido.FINALIZADO) {

                Map<String, String> error = new HashMap<>();
                error.put("error", "Pedido não pode ser finalizado. Status atual: " + pedido.getStatus());
                return ResponseEntity.badRequest().body(error);
            }

            // Atualiza status
            pedido.setStatus(StatusPedido.FINALIZADO);
            pedido.setDataEntrega(LocalDate.now());

            Pedido pedidoSalvo = pedidoRepository.save(pedido);

            System.out.println("✅ Pedido finalizado com sucesso");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido finalizado com sucesso");
            response.put("pedido", Map.of(
                    "id", pedidoSalvo.getId(),
                    "codigo", pedidoSalvo.getCodigo(),
                    "status", pedidoSalvo.getStatus().toString(),
                    "dataEntrega", pedidoSalvo.getDataEntrega()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro ao finalizar pedido: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Erro interno: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // ============ MÉTODOS DE BUSCA POR RELACIONAMENTO (atualizados para DTO) ============

    // Listar pedidos por dentista - AGORA RETORNA DTO
    @GetMapping("/dentista/{dentistaId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorDentista(@PathVariable Long dentistaId) {
        List<Pedido> pedidos = pedidoRepository.findByDentistaId(dentistaId);
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    // Listar pedidos por protético - AGORA RETORNA DTO
    @GetMapping("/protetico/{proteticoId}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorProtetico(@PathVariable Long proteticoId) {
        List<Pedido> pedidos = pedidoRepository.findByProteticoId(proteticoId);
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    // Listar pedidos por status - AGORA RETORNA DTO
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorStatus(@PathVariable StatusPedido status) {
        List<Pedido> pedidos = pedidoRepository.findByStatus(status);
        List<PedidoResponseDTO> dtos = pedidoService.converterListaParaDTO(pedidos);
        return ResponseEntity.ok(dtos);
    }

    // ============ MÉTODOS ESPECIAIS PARA FRONTEND (mantidos como Map) ============

    @GetMapping("/para-frontend/{dentistaId}")
    public ResponseEntity<List<Map<String, Object>>> paraFrontend(@PathVariable Long dentistaId) {
        try {
            System.out.println("=== GERANDO DADOS PARA FRONTEND - DENTISTA " + dentistaId + " ===");

            // Busca pedidos do dentista
            List<Pedido> pedidos = pedidoRepository.findByDentistaId(dentistaId);
            System.out.println("Encontrados " + pedidos.size() + " pedidos");

            List<Map<String, Object>> response = new ArrayList<>();

            for (Pedido p : pedidos) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", p.getId());
                item.put("codigo", p.getCodigo());

                // Tipo de serviço - trata null
                String tipoServicoStr = "NÃO_ESPECIFICADO";
                if (p.getTipoServico() != null) {
                    tipoServicoStr = p.getTipoServico().name();
                }
                item.put("tipoServico", tipoServicoStr);

                // Status - trata null
                String statusStr = "RASCUNHO";
                if (p.getStatus() != null) {
                    statusStr = p.getStatus().name();
                }
                item.put("status", statusStr);

                item.put("dataEntrada", p.getDataEntrada());
                item.put("dataPrevistaEntrega", p.getDataPrevistaEntrega());
                item.put("valorCobrado", p.getValorCobrado());
                item.put("informacoesDetalhadas", p.getInformacoesDetalhadas());

                // Informações do protético
                Map<String, Object> proteticoInfo = new HashMap<>();
                if (p.getProtetico() != null) {
                    proteticoInfo.put("nome", p.getProtetico().getNome());
                    proteticoInfo.put("id", p.getProtetico().getId());
                    proteticoInfo.put("email", p.getProtetico().getEmail());
                } else {
                    proteticoInfo.put("nome", "Protético não definido");
                    proteticoInfo.put("id", 0);
                    proteticoInfo.put("email", "");
                }
                item.put("protetico", proteticoInfo);

                // Informações do dentista (opcional)
                Map<String, Object> dentistaInfo = new HashMap<>();
                if (p.getDentista() != null) {
                    dentistaInfo.put("nome", p.getDentista().getNome());
                    dentistaInfo.put("id", p.getDentista().getId());
                }
                item.put("dentista", dentistaInfo);

                response.add(item);
            }

            System.out.println("✅ Dados gerados: " + response.size() + " itens");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro no para-frontend: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(Collections.singletonList(error));
        }
    }

    @GetMapping("/para-frontend-protetico/{proteticoId}")
    public ResponseEntity<List<Map<String, Object>>> paraFrontendProtetico(@PathVariable Long proteticoId) {
        try {
            System.out.println("=== GERANDO DADOS PARA FRONTEND - PROTÉTICO " + proteticoId + " ===");

            // Busca pedidos do protético
            List<Pedido> pedidos = pedidoRepository.findByProteticoId(proteticoId);
            System.out.println("Encontrados " + pedidos.size() + " pedidos para este protético");

            List<Map<String, Object>> response = new ArrayList<>();

            for (Pedido p : pedidos) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", p.getId());
                item.put("codigo", p.getCodigo());

                // Tipo de serviço
                String tipoServicoStr = "NÃO_ESPECIFICADO";
                if (p.getTipoServico() != null) {
                    tipoServicoStr = p.getTipoServico().name();
                }
                item.put("tipoServico", tipoServicoStr);

                // Status
                String statusStr = "RASCUNHO";
                if (p.getStatus() != null) {
                    statusStr = p.getStatus().name();
                }
                item.put("status", statusStr);

                item.put("dataEntrada", p.getDataEntrada());
                item.put("dataPrevistaEntrega", p.getDataPrevistaEntrega());
                item.put("valorCobrado", p.getValorCobrado());
                item.put("informacoesDetalhadas", p.getInformacoesDetalhadas());

                // Informações do DENTISTA
                Map<String, Object> dentistaInfo = new HashMap<>();
                if (p.getDentista() != null) {
                    dentistaInfo.put("nome", p.getDentista().getNome());
                    dentistaInfo.put("id", p.getDentista().getId());
                    dentistaInfo.put("cro", p.getDentista().getCro());
                    dentistaInfo.put("especialidade", p.getDentista().getEspecialidade());
                } else {
                    dentistaInfo.put("nome", "Dentista não definido");
                    dentistaInfo.put("id", 0);
                }
                item.put("dentista", dentistaInfo);

                // Informações do protético
                Map<String, Object> proteticoInfo = new HashMap<>();
                if (p.getProtetico() != null) {
                    proteticoInfo.put("nome", p.getProtetico().getNome());
                    proteticoInfo.put("id", p.getProtetico().getId());
                }
                item.put("protetico", proteticoInfo);

                response.add(item);
            }

            System.out.println("✅ Dados gerados para protético: " + response.size() + " itens");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Erro no para-frontend-protetico: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(Collections.singletonList(error));
        }
    }
}