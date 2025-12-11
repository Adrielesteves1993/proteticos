package com.proteticos.ordermanagement.controller;

import com.proteticos.ordermanagement.DTO.ProteticoDTO;
import com.proteticos.ordermanagement.model.Protetico;
import com.proteticos.ordermanagement.service.ProteticoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proteticos")
public class ProteticoController {

    @Autowired
    private ProteticoService proteticoService;

    @GetMapping
    public ResponseEntity<List<ProteticoDTO>> listarTodos() {
        List<ProteticoDTO> proteticos = proteticoService.listarTodos();
        return ResponseEntity.ok(proteticos);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<ProteticoDTO>> listarAtivos() {
        List<ProteticoDTO> proteticos = proteticoService.listarAtivos();
        return ResponseEntity.ok(proteticos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProteticoDTO> buscarPorId(@PathVariable Long id) {
        ProteticoDTO protetico = proteticoService.buscarPorId(id);
        if (protetico == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(protetico);
    }

    @PostMapping
    public ResponseEntity<ProteticoDTO> criarProtetico(@Valid @RequestBody Protetico protetico) {
        try {
            ProteticoDTO novo = proteticoService.criarProtetico(protetico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProteticoDTO> atualizarProtetico(
            @PathVariable Long id,
            @Valid @RequestBody ProteticoDTO proteticoDTO) {
        try {
            ProteticoDTO atualizado = proteticoService.atualizarProtetico(id, proteticoDTO);
            return ResponseEntity.ok(atualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativarProtetico(@PathVariable Long id) {
        try {
            proteticoService.desativarProtetico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProteticoDTO>> buscarPorEspecializacao(
            @RequestParam String especializacao) {
        List<ProteticoDTO> proteticos = proteticoService.buscarPorEspecializacao(especializacao);
        return ResponseEntity.ok(proteticos);
    }

    @GetMapping("/buscar/nome")
    public ResponseEntity<List<ProteticoDTO>> buscarPorNome(@RequestParam String nome) {
        List<ProteticoDTO> proteticos = proteticoService.buscarPorNome(nome);
        return ResponseEntity.ok(proteticos);
    }
}