// controller/CadastroController.java
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

import java.math.BigDecimal;
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

            // ✅✅✅ 5. Criar usuário CORRETAMENTE (usando herança) ✅✅✅
            Usuario novoUsuario;

            if (request.getTipoUsuario() == UserTipo.DENTISTA) {
                System.out.println("🦷 Criando DENTISTA...");

                // ✅ Cria como Dentista desde o início
                Dentista dentista = new Dentista(
                        request.getNome(),
                        request.getEmail(),
                        request.getSenha(),
                        request.getCro() != null ? request.getCro() : "A DEFINIR",
                        request.getEspecialidade() != null ? request.getEspecialidade() : "Clínica Geral"
                );
                dentista.setAtivo(true);

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

                // ✅ Cria como Protético desde o início
                Protetico protetico = new Protetico(
                        request.getNome(),
                        request.getEmail(),
                        request.getSenha(),
                        request.getRegistroProfissional() != null ? request.getRegistroProfissional() : "A DEFINIR",
                        request.getEspecializacao() != null ? request.getEspecializacao() : "Protética Geral"
                );
                protetico.setAtivo(true);

                // Campos adicionais do protético
                if (request.getAceitaTerceirizacao() != null) {
                    protetico.setAceitaTerceirizacao(request.getAceitaTerceirizacao());
                }
                if (request.getValorHora() != null) {
                    protetico.setValorHora(request.getValorHora());
                } else {
                    protetico.setValorHora(BigDecimal.valueOf(150.00)); // Valor padrão
                }
                if (request.getCapacidadePedidosSimultaneos() != null) {
                    protetico.setCapacidadePedidosSimultaneos(request.getCapacidadePedidosSimultaneos());
                }

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
                novoUsuario.setAtivo(true);
            }

            // ✅ Salva UMA VEZ - JPA cuida de salvar nas tabelas corretas
            Usuario usuarioSalvo = usuarioService.salvar(novoUsuario);
            System.out.println("💾 Usuário salvo com ID: " + usuarioSalvo.getId());

            // 6. Marcar convite como utilizado
            conviteService.marcarComoUtilizado(convite, usuarioSalvo);
            System.out.println("🎫 Convite marcado como utilizado");

            // 7. Retornar sucesso
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuário cadastrado com sucesso");
            response.put("usuario", usuarioSalvo);
            response.put("tipo", usuarioSalvo.getTipo());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ ERRO no cadastro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Erro interno: " + e.getMessage())
            );
        }
    }
}