package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.model.*;
import com.proteticos.ordermanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.proteticos.ordermanagement.model.LoginRequest;
import com.proteticos.ordermanagement.model.Usuario;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DentistaRepository dentistaRepository;

    @Autowired
    private ProteticoRepository proteticoRepository;

    // Listar todos os usuários
    @GetMapping
    public List<?> listarTodos() {
        return usuarioRepository.findAll();
    }

    // Login de usuário
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔐 Tentativa de login: " + loginRequest.getEmail());

            // Buscar todos os usuários
            List<Usuario> usuarios = usuarioRepository.findAll();
            System.out.println("📊 Total de usuários: " + usuarios.size());

            // Encontrar usuário pelo email e senha
            Usuario usuarioEncontrado = usuarios.stream()
                    .filter(usuario ->
                            usuario.getEmail().equals(loginRequest.getEmail()) &&
                                    usuario.getSenha().equals(loginRequest.getSenha())
                    )
                    .findFirst()
                    .orElse(null);

            if (usuarioEncontrado != null) {
                System.out.println("✅ Login bem-sucedido: " + usuarioEncontrado.getNome());
                return ResponseEntity.ok(usuarioEncontrado);
            } else {
                System.out.println("❌ Credenciais inválidas");
                return ResponseEntity.status(401).body("Credenciais inválidas");
            }
        } catch (Exception e) {
            System.out.println("💥 Erro no login: " + e.getMessage());
            return ResponseEntity.status(500).body("Erro interno no servidor");
        }
    }

    // Criar um dentista
    @PostMapping("/dentistas")
    public ResponseEntity<Dentista> criarDentista(@RequestBody Dentista dentista) {
        dentista.setTipo(UserTipo.DENTISTA);
        Dentista salvo = dentistaRepository.save(dentista);
        return ResponseEntity.ok(salvo);
    }

    // Criar um protético
    @PostMapping("/proteticos")
    public ResponseEntity<Protetico> criarProtetico(@RequestBody Protetico protetico) {
        protetico.setTipo(UserTipo.PROTETICO);
        Protetico salvo = proteticoRepository.save(protetico);
        return ResponseEntity.ok(salvo);
    }

    // Listar todos os dentistas
    @GetMapping("/dentistas")
    public List<Dentista> listarDentistas() {
        return dentistaRepository.findAll();
    }

    // Listar todos os protéticos
    @GetMapping("/proteticos")
    public List<Protetico> listarProteticos() {
        return proteticoRepository.findAll();
    }
    @GetMapping("/teste")
    public String teste() {
        return "✅ UsuarioController funcionando!";
    }
}