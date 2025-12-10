// model/Convite.java
package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "convites")
public class Convite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Mude para IDENTITY
    private Long id; // Mude para Long

    @Column(unique = true, nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserTipo tipo;

    private String emailConvidado;

    @ManyToOne
    @JoinColumn(name = "criado_por_id", nullable = false)
    private Usuario criadoPor;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    private boolean utilizado = false;

    private LocalDateTime utilizadoEm;

    @ManyToOne
    @JoinColumn(name = "utilizado_por_id")
    private Usuario utilizadoPor;

    // Construtores
    public Convite() {}

    // Getters e Setters - TODOS COM LONG
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public UserTipo getTipo() { return tipo; }
    public void setTipo(UserTipo tipo) { this.tipo = tipo; }

    public String getEmailConvidado() { return emailConvidado; }
    public void setEmailConvidado(String emailConvidado) { this.emailConvidado = emailConvidado; }

    public Usuario getCriadoPor() { return criadoPor; }
    public void setCriadoPor(Usuario criadoPor) { this.criadoPor = criadoPor; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(LocalDateTime expiraEm) { this.expiraEm = expiraEm; }

    public boolean isUtilizado() { return utilizado; }
    public void setUtilizado(boolean utilizado) { this.utilizado = utilizado; }

    public LocalDateTime getUtilizadoEm() { return utilizadoEm; }
    public void setUtilizadoEm(LocalDateTime utilizadoEm) { this.utilizadoEm = utilizadoEm; }

    public Usuario getUtilizadoPor() { return utilizadoPor; }
    public void setUtilizadoPor(Usuario utilizadoPor) { this.utilizadoPor = utilizadoPor; }
}