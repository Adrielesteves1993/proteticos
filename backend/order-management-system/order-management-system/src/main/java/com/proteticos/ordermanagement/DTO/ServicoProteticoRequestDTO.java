package com.proteticos.ordermanagement.DTO;

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
    private Integer tempoMedioDias; // MUDOU: horas → dias

    private boolean ativo = true;

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

    // GETTER E SETTER para DIAS
    public Integer getTempoMedioDias() {
        return tempoMedioDias;
    }

    public void setTempoMedioDias(Integer tempoMedioDias) {
        this.tempoMedioDias = tempoMedioDias;
    }

    // MANTÉM getter/setter antigo para compatibilidade (opcional)
    @Deprecated
    public Integer getTempoMedioHoras() {
        return tempoMedioDias != null ? tempoMedioDias * 24 : null;
    }

    @Deprecated
    public void setTempoMedioHoras(Integer tempoMedioHoras) {
        this.tempoMedioDias = tempoMedioHoras != null ? tempoMedioHoras / 24 : null;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}