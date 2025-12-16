package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.PoliticaExecucaoServico;
import java.math.BigDecimal;

public class AtualizarServicoRequestDTO {
    private BigDecimal preco;
    private String descricao;
    private Integer tempoMedioDias;
    private Boolean ativo;

    // NOVOS CAMPOS para terceirização
    private PoliticaExecucaoServico politicaExecucao;
    private BigDecimal precoTerceirizado;
    private Integer prazoTerceirizadoDias;
    private Long terceirizadoPreferidoId;
    private String observacoesTerceirizacao;

    // Getters e Setters
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getTempoMedioDias() { return tempoMedioDias; }
    public void setTempoMedioDias(Integer tempoMedioDias) { this.tempoMedioDias = tempoMedioDias; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public PoliticaExecucaoServico getPoliticaExecucao() { return politicaExecucao; }
    public void setPoliticaExecucao(PoliticaExecucaoServico politicaExecucao) { this.politicaExecucao = politicaExecucao; }

    public BigDecimal getPrecoTerceirizado() { return precoTerceirizado; }
    public void setPrecoTerceirizado(BigDecimal precoTerceirizado) { this.precoTerceirizado = precoTerceirizado; }

    public Integer getPrazoTerceirizadoDias() { return prazoTerceirizadoDias; }
    public void setPrazoTerceirizadoDias(Integer prazoTerceirizadoDias) { this.prazoTerceirizadoDias = prazoTerceirizadoDias; }

    public Long getTerceirizadoPreferidoId() { return terceirizadoPreferidoId; }
    public void setTerceirizadoPreferidoId(Long terceirizadoPreferidoId) { this.terceirizadoPreferidoId = terceirizadoPreferidoId; }

    public String getObservacoesTerceirizacao() { return observacoesTerceirizacao; }
    public void setObservacoesTerceirizacao(String observacoesTerceirizacao) { this.observacoesTerceirizacao = observacoesTerceirizacao; }
}