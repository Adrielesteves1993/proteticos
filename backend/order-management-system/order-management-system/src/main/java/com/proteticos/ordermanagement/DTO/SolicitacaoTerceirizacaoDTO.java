package com.proteticos.ordermanagement.DTO;

public class SolicitacaoTerceirizacaoDTO {
    private Long proteticoOrigemId;
    private Long proteticoDestinoId;
    private Long pedidoId;
    private String servicoSolicitado;
    private String observacoes;
    private Double valorCombinado;

    // Construtor
    public SolicitacaoTerceirizacaoDTO() {}

    // Getters e Setters (COLOCAR TODOS)
    public Long getProteticoOrigemId() { return proteticoOrigemId; }
    public void setProteticoOrigemId(Long proteticoOrigemId) { this.proteticoOrigemId = proteticoOrigemId; }

    public Long getProteticoDestinoId() { return proteticoDestinoId; }
    public void setProteticoDestinoId(Long proteticoDestinoId) { this.proteticoDestinoId = proteticoDestinoId; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getServicoSolicitado() { return servicoSolicitado; }
    public void setServicoSolicitado(String servicoSolicitado) { this.servicoSolicitado = servicoSolicitado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Double getValorCombinado() { return valorCombinado; }
    public void setValorCombinado(Double valorCombinado) { this.valorCombinado = valorCombinado; }
}