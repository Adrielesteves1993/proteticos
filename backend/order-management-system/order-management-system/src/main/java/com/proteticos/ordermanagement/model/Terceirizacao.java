package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "terceirizacoes")
public class Terceirizacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "protetico_origem_id")
    private Protetico proteticoOrigem;

    @ManyToOne
    @JoinColumn(name = "protetico_destino_id")
    private Protetico proteticoDestino;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    private StatusTerceirizacao status;

    private String servicoSolicitado;
    private String observacoes;
    private Double valorCombinado;

    @CreationTimestamp
    private LocalDateTime solicitadoEm;

    private LocalDateTime aceitoEm;
    private LocalDateTime concluidoEm;

    // Construtores
    public Terceirizacao() {}

    public Terceirizacao(Protetico proteticoOrigem, Protetico proteticoDestino,
                         Pedido pedido, String servicoSolicitado) {
        this.proteticoOrigem = proteticoOrigem;
        this.proteticoDestino = proteticoDestino;
        this.pedido = pedido;
        this.servicoSolicitado = servicoSolicitado;
        this.status = StatusTerceirizacao.SOLICITADA;
    }

    // Getters e Setters (PRECISAMOS DE TODOS!)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Protetico getProteticoOrigem() { return proteticoOrigem; }
    public void setProteticoOrigem(Protetico proteticoOrigem) { this.proteticoOrigem = proteticoOrigem; }

    public Protetico getProteticoDestino() { return proteticoDestino; }
    public void setProteticoDestino(Protetico proteticoDestino) { this.proteticoDestino = proteticoDestino; }

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public StatusTerceirizacao getStatus() { return status; }
    public void setStatus(StatusTerceirizacao status) { this.status = status; }

    public String getServicoSolicitado() { return servicoSolicitado; }
    public void setServicoSolicitado(String servicoSolicitado) { this.servicoSolicitado = servicoSolicitado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Double getValorCombinado() { return valorCombinado; }
    public void setValorCombinado(Double valorCombinado) { this.valorCombinado = valorCombinado; }

    public LocalDateTime getSolicitadoEm() { return solicitadoEm; }
    public void setSolicitadoEm(LocalDateTime solicitadoEm) { this.solicitadoEm = solicitadoEm; }

    public LocalDateTime getAceitoEm() { return aceitoEm; }
    public void setAceitoEm(LocalDateTime aceitoEm) { this.aceitoEm = aceitoEm; }

    public LocalDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(LocalDateTime concluidoEm) { this.concluidoEm = concluidoEm; }
}