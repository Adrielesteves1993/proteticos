package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "servicos_protetico",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"protetico_id", "tipo_servico"})
        })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServicoProtetico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protetico_id", nullable = false)
    private Protetico protetico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servico", nullable = false)
    private TipoServico tipoServico;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(columnDefinition = "boolean default true")
    private boolean ativo = true;

    @Column(length = 500)
    private String descricao;

    // MANTÉM horas no banco mas converte para dias via getter
    @Column(name = "tempo_medio_horas")
    private Integer tempoMedioHoras;

    @Column(name = "data_criacao", updatable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao = LocalDateTime.now();

    // Construtor padrão
    public ServicoProtetico() {}

    // Construtor com parâmetros principais
    public ServicoProtetico(Protetico protetico, TipoServico tipoServico, BigDecimal preco) {
        this.protetico = protetico;
        this.tipoServico = tipoServico;
        this.preco = preco;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Protetico getProtetico() { return protetico; }
    public void setProtetico(Protetico protetico) { this.protetico = protetico; }

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // MANTÉM horas no banco
    public Integer getTempoMedioHoras() {
        return tempoMedioHoras;
    }

    public void setTempoMedioHoras(Integer tempoMedioHoras) {
        this.tempoMedioHoras = tempoMedioHoras;
    }

    // NOVO: Getter para dias (converte horas para dias)
    @Transient // Não persiste no banco
    public Integer getTempoMedioDias() {
        if (tempoMedioHoras == null) {
            return null;
        }
        // Converte horas para dias (arredonda para cima)
        return (int) Math.ceil(tempoMedioHoras / 24.0);
    }

    // NOVO: Setter para dias (converte dias para horas)
    @Transient // Não persiste no banco
    public void setTempoMedioDias(Integer tempoMedioDias) {
        if (tempoMedioDias == null) {
            this.tempoMedioHoras = null;
        } else {
            // Converte dias para horas
            this.tempoMedioHoras = tempoMedioDias * 24;
        }
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}