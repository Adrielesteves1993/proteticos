package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;  // ← ADICIONE ESTE IMPORT

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "usuarios")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ← ADICIONE ESTA LINHA
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserTipo tipo;

    private boolean ativo = true;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Construtor padrão (OBRIGATÓRIO para JPA)
    public Usuario() {}

    // Construtor com parâmetros
    public Usuario(String nome, String email, String senha, UserTipo tipo) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // GETTERS E SETTERS (OBRIGATÓRIOS)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public UserTipo getTipo() { return tipo; }
    public void setTipo(UserTipo tipo) { this.tipo = tipo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}