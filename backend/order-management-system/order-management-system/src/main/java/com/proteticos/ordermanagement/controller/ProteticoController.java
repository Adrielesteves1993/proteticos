// controller/ProteticoController.java
package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.repository.ProteticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proteticos")
@CrossOrigin(origins = "http://localhost:3000") // IMPORTANTE para o Next.js
public class ProteticoController {

    @Autowired
    private ProteticoRepository proteticoRepository;

    // GET - Listar todos os protéticos ATIVOS (o que seu front-end precisa)
    @GetMapping
    public ResponseEntity<List<Protetico>> listarProteticosAtivos() {
        try {
            List<Protetico> proteticos = proteticoRepository.findByAtivoTrue();
            return ResponseEntity.ok(proteticos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET - Buscar protético por ID
    @GetMapping("/{id}")
    public ResponseEntity<Protetico> buscarProteticoPorId(@PathVariable Long id) {
        try {
            Optional<Protetico> protetico = proteticoRepository.findById(id);
            return protetico.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET - Buscar por especialização
    @GetMapping("/especializacao/{especializacao}")
    public ResponseEntity<List<Protetico>> buscarPorEspecializacao(@PathVariable String especializacao) {
        try {
            List<Protetico> proteticos = proteticoRepository.findByEspecializacao(especializacao);
            return ResponseEntity.ok(proteticos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // GET - Protéticos que aceitam terceirização
    @GetMapping("/terceirizacao")
    public ResponseEntity<List<Protetico>> listarParaTerceirizacao() {
        try {
            List<Protetico> proteticos = proteticoRepository.findByAceitaTerceirizacaoTrueAndAtivoTrue();
            return ResponseEntity.ok(proteticos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}