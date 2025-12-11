package com.proteticos.ordermanagement.DTO;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class AtualizarServicoRequestDTO {

    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal preco;

    @Positive(message = "Tempo médio deve ser maior que zero")
    private Integer tempoMedioDias;

    private String descricao;

    // Getters e Setters
    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getTempoMedioDias() {
        return tempoMedioDias;
    }

    public void setTempoMedioDias(Integer tempoMedioDias) {
        this.tempoMedioDias = tempoMedioDias;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}