package com.proteticos.ordermanagement.dto;

import com.proteticos.ordermanagement.model.StatusEtapa;
import java.time.LocalDateTime;

public class EtapaPedidoDTO {
    private Long id;
    private String nomeEtapa;
    private String observacoes;
    private StatusEtapa status;
    private Integer ordem;
    private LocalDateTime dataConclusao;
    private LocalDateTime dataCriacao;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeEtapa() { return nomeEtapa; }
    public void setNomeEtapa(String nomeEtapa) { this.nomeEtapa = nomeEtapa; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public StatusEtapa getStatus() { return status; }
    public void setStatus(StatusEtapa status) { this.status = status; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}