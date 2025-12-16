package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.PoliticaExecucaoServico; // NOVO IMPORT
import com.proteticos.ordermanagement.model.TipoServico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ServicoProteticoRequestDTO {

    @NotNull(message = "Tipo de serviço é obrigatório")
    private TipoServico tipoServico;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    private String descricao;

    @Positive(message = "Tempo médio deve ser positivo")
    private Integer tempoMedioDias;

    private boolean ativo = true;

    // NOVOS CAMPOS PARA TERCEIRIZAÇÃO
    @NotNull(message = "Política de execução é obrigatória")
    private PoliticaExecucaoServico politicaExecucao = PoliticaExecucaoServico.PROPRIO;

    @Positive(message = "Preço terceirizado deve ser positivo")
    private BigDecimal precoTerceirizado;

    @Positive(message = "Prazo terceirizado deve ser positivo")
    private Integer prazoTerceirizadoDias;

    private Long terceirizadoPreferidoId;
    private String observacoesTerceirizacao;

    // GETTERS E SETTERS
    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTempoMedioDias() {
        return tempoMedioDias;
    }

    public void setTempoMedioDias(Integer tempoMedioDias) {
        this.tempoMedioDias = tempoMedioDias;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // NOVOS GETTERS E SETTERS
    public PoliticaExecucaoServico getPoliticaExecucao() {
        return politicaExecucao;
    }

    public void setPoliticaExecucao(PoliticaExecucaoServico politicaExecucao) {
        this.politicaExecucao = politicaExecucao;
    }

    public BigDecimal getPrecoTerceirizado() {
        return precoTerceirizado;
    }

    public void setPrecoTerceirizado(BigDecimal precoTerceirizado) {
        this.precoTerceirizado = precoTerceirizado;
    }

    public Integer getPrazoTerceirizadoDias() {
        return prazoTerceirizadoDias;
    }

    public void setPrazoTerceirizadoDias(Integer prazoTerceirizadoDias) {
        this.prazoTerceirizadoDias = prazoTerceirizadoDias;
    }

    public Long getTerceirizadoPreferidoId() {
        return terceirizadoPreferidoId;
    }

    public void setTerceirizadoPreferidoId(Long terceirizadoPreferidoId) {
        this.terceirizadoPreferidoId = terceirizadoPreferidoId;
    }

    public String getObservacoesTerceirizacao() {
        return observacoesTerceirizacao;
    }

    public void setObservacoesTerceirizacao(String observacoesTerceirizacao) {
        this.observacoesTerceirizacao = observacoesTerceirizacao;
    }
}