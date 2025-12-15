package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.TipoTerceirizacao;
import java.math.BigDecimal;

public class SolicitarTerceirizacaoRequest {
    private Long proteticoTerceirizadoId;
    private BigDecimal percentual;
    private TipoTerceirizacao tipo;
    private String motivo;
    private String descricaoServico;

    // Construtor padrão
    public SolicitarTerceirizacaoRequest() {}

    // Construtor com parâmetros
    public SolicitarTerceirizacaoRequest(Long proteticoTerceirizadoId, BigDecimal percentual,
                                         TipoTerceirizacao tipo, String motivo, String descricaoServico) {
        this.proteticoTerceirizadoId = proteticoTerceirizadoId;
        this.percentual = percentual;
        this.tipo = tipo;
        this.motivo = motivo;
        this.descricaoServico = descricaoServico;
    }

    // Getters e Setters
    public Long getProteticoTerceirizadoId() {
        return proteticoTerceirizadoId;
    }

    public void setProteticoTerceirizadoId(Long proteticoTerceirizadoId) {
        this.proteticoTerceirizadoId = proteticoTerceirizadoId;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public TipoTerceirizacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTerceirizacao tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    @Override
    public String toString() {
        return "SolicitarTerceirizacaoRequest{" +
                "proteticoTerceirizadoId=" + proteticoTerceirizadoId +
                ", percentual=" + percentual +
                ", tipo=" + tipo +
                ", motivo='" + motivo + '\'' +
                ", descricaoServico='" + descricaoServico + '\'' +
                '}';
    }
}