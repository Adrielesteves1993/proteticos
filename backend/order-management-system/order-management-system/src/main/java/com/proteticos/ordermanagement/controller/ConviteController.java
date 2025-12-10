// controller/ConviteController.java
package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.model.Convite;
import com.proteticos.ordermanagement.model.UserTipo;
import com.proteticos.ordermanagement.model.Usuario;
import com.proteticos.ordermanagement.repository.ConviteRepository;
import com.proteticos.ordermanagement.repository.UsuarioRepository;
import com.proteticos.ordermanagement.service.ConviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/convites")
@CrossOrigin(origins = "http://localhost:3000") // ← ADICIONE para permitir front-end
public class ConviteController {

    @Autowired
    private ConviteService conviteService;

    @Autowired // ← ADICIONE para listar convites
    private ConviteRepository conviteRepository;

    @Autowired // ← ADICIONE para buscar usuário real
    private UsuarioRepository usuarioRepository;

    // ✅ 1. Listar todos os convites (para a página de gerenciamento)
    @GetMapping
    public ResponseEntity<List<Convite>> listarConvites() {
        try {
            List<Convite> convites = conviteRepository.findAll();
            return ResponseEntity.ok(convites);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ 2. Criar novo convite (atualizado)
    @PostMapping
    public ResponseEntity<?> criarConvite(
            @RequestBody CriarConviteRequest request,
            @RequestHeader(value = "usuarioId", required = false) Long usuarioId) { // Tornado opcional para teste

        try {
            System.out.println("=== GERANDO CONVITE ===");
            System.out.println("Tipo: " + request.getTipo());
            System.out.println("Email: " + request.getEmailConvidado());
            System.out.println("Usuário ID: " + usuarioId);

            // Buscar usuário real do banco
            Usuario usuarioCriador;
            if (usuarioId != null) {
                Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
                if (usuarioOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("error", "Usuário não encontrado")
                    );
                }
                usuarioCriador = usuarioOpt.get();
            } else {
                // Modo teste: usar primeiro usuário encontrado
                List<Usuario> usuarios = usuarioRepository.findAll();
                if (usuarios.isEmpty()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("error", "Nenhum usuário cadastrado no sistema")
                    );
                }
                usuarioCriador = usuarios.get(0);
                System.out.println("⚠️ Usando usuário teste: " + usuarioCriador.getNome());
            }

            // Criar convite
            Convite convite = conviteService.criarConvite(
                    request.getTipo(),
                    request.getEmailConvidado(),
                    usuarioCriador
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Convite gerado com sucesso!");
            response.put("convite", convite);
            response.put("codigo", convite.getCodigo());
            response.put("tipo", convite.getTipo());
            response.put("emailConvidado", convite.getEmailConvidado());
            response.put("expiraEm", convite.getExpiraEm());
            response.put("criadoPor", convite.getCriadoPor().getNome());
            response.put("linkCadastro", "http://localhost:3000/cadastro/" + convite.getCodigo());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro interno: " + e.getMessage())
            );
        }
    }

    // ✅ 3. Endpoint SIMPLIFICADO para teste (sem necessidade de header)
    @PostMapping("/gerar-teste")
    public ResponseEntity<?> gerarConviteTeste(@RequestBody CriarConviteRequest request) {
        try {
            System.out.println("=== GERANDO CONVITE (TESTE) ===");
            System.out.println("Tipo: " + request.getTipo());
            System.out.println("Email: " + request.getEmailConvidado());

            // Buscar qualquer usuário para teste
            List<Usuario> usuarios = usuarioRepository.findAll();
            if (usuarios.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Nenhum usuário cadastrado. Cadastre um usuário primeiro.")
                );
            }

            Usuario usuarioTeste = usuarios.get(0);

            Convite convite = conviteService.criarConvite(
                    request.getTipo(),
                    request.getEmailConvidado(),
                    usuarioTeste
            );

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Convite de teste gerado com sucesso!");
            response.put("codigo", convite.getCodigo());
            response.put("tipo", convite.getTipo());
            response.put("expiraEm", convite.getExpiraEm());
            response.put("link", "http://localhost:3000/cadastro/" + convite.getCodigo());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro interno: " + e.getMessage())
            );
        }
    }

    // ✅ 4. Validar convite (para página de cadastro)
    @GetMapping("/{codigo}/validar")
    public ResponseEntity<?> validarConvite(@PathVariable String codigo) {
        try {
            Optional<Convite> convite = conviteService.validarConvite(codigo);
            if (convite.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("valido", true);
                response.put("convite", convite.get());
                response.put("tipo", convite.get().getTipo());
                response.put("emailConvidado", convite.get().getEmailConvidado());
                response.put("expiraEm", convite.get().getExpiraEm());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("valido", false);
                response.put("error", "Convite inválido, expirado ou já utilizado");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Erro ao validar convite")
            );
        }
    }

    // ✅ 5. Buscar convite por código
    @GetMapping("/{codigo}")
    public ResponseEntity<?> buscarPorCodigo(@PathVariable String codigo) {
        try {
            Optional<Convite> conviteOpt = conviteRepository.findByCodigo(codigo);
            if (conviteOpt.isPresent()) {
                return ResponseEntity.ok(conviteOpt.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ 6. Deletar convite (se não utilizado)
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> deletarConvite(@PathVariable String codigo) {
        try {
            Optional<Convite> conviteOpt = conviteRepository.findByCodigo(codigo);
            if (conviteOpt.isPresent()) {
                Convite convite = conviteOpt.get();
                if (convite.isUtilizado()) {
                    return ResponseEntity.badRequest().body(
                            Map.of("error", "Não é possível deletar um convite já utilizado")
                    );
                }
                conviteRepository.delete(convite);
                return ResponseEntity.ok(Map.of("message", "Convite deletado com sucesso"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ 7. Listar convites por criador
    @GetMapping("/criador/{usuarioId}")
    public ResponseEntity<List<Convite>> listarPorCriador(@PathVariable Long usuarioId) {
        try {
            List<Convite> convites = conviteRepository.findByCriadoPorId(usuarioId);
            return ResponseEntity.ok(convites);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO interno
    public static class CriarConviteRequest {
        private UserTipo tipo;
        private String emailConvidado;

        public UserTipo getTipo() { return tipo; }
        public void setTipo(UserTipo tipo) { this.tipo = tipo; }

        public String getEmailConvidado() { return emailConvidado; }
        public void setEmailConvidado(String emailConvidado) { this.emailConvidado = emailConvidado; }
    }
}