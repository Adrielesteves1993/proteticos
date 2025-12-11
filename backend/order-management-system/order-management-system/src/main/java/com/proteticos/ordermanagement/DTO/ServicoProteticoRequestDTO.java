
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
    private Integer tempoMedioHoras;

    private boolean ativo = true;

    // GETTERS E SETTERS
    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Integer getTempoMedioHoras() { return tempoMedioHoras; }
    public void setTempoMedioHoras(Integer tempoMedioHoras) { this.tempoMedioHoras = tempoMedioHoras; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}