package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.controller.dto.CadastroRequest;
import com.proteticos.ordermanagement.model.Convite;
import com.proteticos.ordermanagement.model.UserTipo;
import com.proteticos.ordermanagement.model.Usuario;
import com.proteticos.ordermanagement.model.Dentista;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.service.ConviteService;
import com.proteticos.ordermanagement.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastro")
public class CadastroController {

    @Autowired
    private ConviteService conviteService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<?> cadastrarUsuario(@RequestBody CadastroRequest request) {
        try {
            System.out.println("📥 Recebendo cadastro: " + request.toString());

            // 1. Validar o convite
            Optional<Convite> conviteOpt = conviteService.validarConvite(request.getCodigoConvite());
            if (conviteOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Convite inválido, expirado ou já utilizado")
                );
            }

            Convite convite = conviteOpt.get();

            // 2. Verificar se o tipo do convite bate com o solicitado
            if (convite.getTipo() != request.getTipoUsuario()) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Tipo de usuário não corresponde ao convite")
                );
            }

            // 3. Verificar se email já existe
            if (usuarioService.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Email já cadastrado")
                );
            }

            // 4. Verificar se email do convite bate (se foi especificado)
            if (convite.getEmailConvidado() != null &&
                    !convite.getEmailConvidado().equals(request.getEmail())) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "Email não corresponde ao convite")
                );
            }

            // 5. Criar usuário
            Usuario novoUsuario;

            if (request.getTipoUsuario() == UserTipo.DENTISTA) {
                System.out.println("🦷 Criando DENTISTA...");

                // ✅ Usando o construtor CORRETO da classe Dentista
                Dentista dentista = new Dentista(
                        request.getNome(),
                        request.getEmail(),
                        request.getSenha(), // Será armazenado como senhaHash no Usuario
                        request.getCro() != null ? request.getCro() : "A DEFINIR",
                        request.getEspecialidade() != null ? request.getEspecialidade() : "Clínica Geral"
                );

                // Campos adicionais do dentista
                if (request.getTelefone() != null) {
                    dentista.setTelefone(request.getTelefone());
                }
                if (request.getEnderecoClinica() != null) {
                    dentista.setEnderecoClinica(request.getEnderecoClinica());
                }

                novoUsuario = dentista;
                System.out.println("✅ Dentista criado: " + dentista.getNome());

            } else if (request.getTipoUsuario() == UserTipo.PROTETICO) {
                System.out.println("🦺 Criando PROTÉTICO...");

                // ✅ AGORA USA CONSTRUTOR CORRETO (igual ao Dentista)
                Protetico protetico = new Protetico(
                        request.getNome(),
                        request.getEmail(),
                        request.getSenha(),  // Será armazenado como 'senha' no Usuario
                        request.getRegistroProfissional() != null ?
                                request.getRegistroProfissional() : "A DEFINIR",
                        request.getEspecializacao() != null ?
                                request.getEspecializacao() : "Protética Geral"
                );

                // Campos adicionais
                if (request.getTelefone() != null) {
                    protetico.setTelefone(request.getTelefone());
                }



                // ✅ NÃO precisa setTipo() - já está no construtor
                novoUsuario = protetico;
                System.out.println("✅ Protético criado: " + protetico.getNome());
            } else {
                // Para outros tipos (ADMIN, etc.)
                System.out.println("👤 Criando USUÁRIO genérico...");
                novoUsuario = new Usuario(
                        request.getNome(),
                        request.getEmail(),
                        request.getSenha(),
                        request.getTipoUsuario()
                );
            }

            // ✅ Salva o usuário
            Usuario usuarioSalvo = usuarioService.salvar(novoUsuario);
            System.out.println("💾 Usuário salvo com ID: " + usuarioSalvo.getId());

            // 6. Marcar convite como utilizado
            conviteService.marcarComoUtilizado(convite, usuarioSalvo);
            System.out.println("🎫 Convite marcado como utilizado");

            // 7. Retornar sucesso
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuário cadastrado com sucesso");
            response.put("usuarioId", usuarioSalvo.getId());
            response.put("tipo", usuarioSalvo.getTipo());
            response.put("nome", usuarioSalvo.getNome());
            response.put("email", usuarioSalvo.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ ERRO no cadastro: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro no cadastro: " + e.getMessage());

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}