// src/main/java/com/proteticos/ordermanagement/controller/AuthController.java
package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.model.Usuario;
import com.proteticos.ordermanagement.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // CORREÇÃO AQUI: Use orElseThrow() para converter Optional<Usuario> para Usuario
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Ou se preferir com tratamento de null:
        // Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
        //         .orElse(null);

        if (usuario == null || !usuario.getSenha().equals(loginRequest.getSenha())) {
            return ResponseEntity.badRequest().body("Credenciais inválidas");
        }

        // ... resto do código de autenticação

        return ResponseEntity.ok("Login bem-sucedido");
    }

    // Classe interna para a requisição de login
    public static class LoginRequest {
        private String email;
        private String senha;

        // getters e setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSenha() {
            return senha;
        }

        public void setSenha(String senha) {
            this.senha = senha;
        }
    }
}