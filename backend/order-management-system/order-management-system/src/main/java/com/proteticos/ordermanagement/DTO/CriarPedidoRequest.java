package com.proteticos.ordermanagement.dto;

import com.proteticos.ordermanagement.model.TipoServico;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CriarPedidoRequest {
    private Long dentistaId;
    private Long proteticoId;
    private TipoServico tipoServico;
    private String informacoesDetalhadas;
    private BigDecimal valorCobrado;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaEntrega;
    private boolean criarEtapasIniciais = true;

    // Construtor padrão
    public CriarPedidoRequest() {}

    // Getters e Setters
    public Long getDentistaId() { return dentistaId; }
    public void setDentistaId(Long dentistaId) { this.dentistaId = dentistaId; }

    public Long getProteticoId() { return proteticoId; }
    public void setProteticoId(Long proteticoId) { this.proteticoId = proteticoId; }

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) { this.tipoServico = tipoServico; }

    public String getInformacoesDetalhadas() { return informacoesDetalhadas; }
    public void setInformacoesDetalhadas(String informacoesDetalhadas) { this.informacoesDetalhadas = informacoesDetalhadas; }

    public BigDecimal getValorCobrado() { return valorCobrado; }
    public void setValorCobrado(BigDecimal valorCobrado) { this.valorCobrado = valorCobrado; }

    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) { this.dataPrevistaEntrega = dataPrevistaEntrega; }

    public boolean isCriarEtapasIniciais() { return criarEtapasIniciais; }
    public void setCriarEtapasIniciais(boolean criarEtapasIniciais) { this.criarEtapasIniciais = criarEtapasIniciais; }
}