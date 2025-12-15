package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TerceirizacaoResponseDTO {
    private Long id;
    private Long pedidoId;
    private String codigoPedido;
    private ProteticoSimplesDTO proteticoSolicitante;
    private ProteticoSimplesDTO proteticoExecutor;
    private BigDecimal valorTerceirizado;
    private BigDecimal percentualTerceirizado;
    private TipoTerceirizacao tipoTerceirizacao;
    private StatusTerceirizacao status;
    private String motivo;
    private String descricaoServico;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataResposta;
    private LocalDateTime dataConclusao;

    // Construtor
    public TerceirizacaoResponseDTO() {}

    // Construtor a partir de um Pedido
    public TerceirizacaoResponseDTO(Pedido pedido) {
        if (pedido == null) return;

        this.pedidoId = pedido.getId();
        this.codigoPedido = pedido.getCodigo();
        this.valorTerceirizado = pedido.getValorTerceirizado();
        this.percentualTerceirizado = pedido.getPercentualTerceirizado();
        this.tipoTerceirizacao = pedido.getTipoTerceirizacao();
        this.status = pedido.getStatusTerceirizacao();
        this.motivo = pedido.getMotivoTerceirizacao();
        this.dataSolicitacao = pedido.getDataSolicitacaoTerceirizacao();
        this.dataResposta = pedido.getDataRespostaTerceirizacao();
        this.dataConclusao = pedido.getDataConclusaoTerceirizacao();

        // Protético solicitante (titular)
        if (pedido.getProtetico() != null) {
            this.proteticoSolicitante = new ProteticoSimplesDTO();
            this.proteticoSolicitante.setId(pedido.getProtetico().getId());
            this.proteticoSolicitante.setNome(pedido.getProtetico().getNome());
            this.proteticoSolicitante.setEmail(pedido.getProtetico().getEmail());
            this.proteticoSolicitante.setRegistroProfissional(pedido.getProtetico().getRegistroProfissional());
        }

        // Protético executor (terceirizado)
        if (pedido.getProteticoTerceirizado() != null) {
            this.proteticoExecutor = new ProteticoSimplesDTO();
            this.proteticoExecutor.setId(pedido.getProteticoTerceirizado().getId());
            this.proteticoExecutor.setNome(pedido.getProteticoTerceirizado().getNome());
            this.proteticoExecutor.setEmail(pedido.getProteticoTerceirizado().getEmail());
            this.proteticoExecutor.setRegistroProfissional(pedido.getProteticoTerceirizado().getRegistroProfissional());
        }
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPedidoId() { return pedidoId; }
    public void setPedidoId(Long pedidoId) { this.pedidoId = pedidoId; }

    public String getCodigoPedido() { return codigoPedido; }
    public void setCodigoPedido(String codigoPedido) { this.codigoPedido = codigoPedido; }

    public ProteticoSimplesDTO getProteticoSolicitante() { return proteticoSolicitante; }
    public void setProteticoSolicitante(ProteticoSimplesDTO proteticoSolicitante) { this.proteticoSolicitante = proteticoSolicitante; }

    public ProteticoSimplesDTO getProteticoExecutor() { return proteticoExecutor; }
    public void setProteticoExecutor(ProteticoSimplesDTO proteticoExecutor) { this.proteticoExecutor = proteticoExecutor; }

    public BigDecimal getValorTerceirizado() { return valorTerceirizado; }
    public void setValorTerceirizado(BigDecimal valorTerceirizado) { this.valorTerceirizado = valorTerceirizado; }

    public BigDecimal getPercentualTerceirizado() { return percentualTerceirizado; }
    public void setPercentualTerceirizado(BigDecimal percentualTerceirizado) { this.percentualTerceirizado = percentualTerceirizado; }

    public TipoTerceirizacao getTipoTerceirizacao() { return tipoTerceirizacao; }
    public void setTipoTerceirizacao(TipoTerceirizacao tipoTerceirizacao) { this.tipoTerceirizacao = tipoTerceirizacao; }

    public StatusTerceirizacao getStatus() { return status; }
    public void setStatus(StatusTerceirizacao status) { this.status = status; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDescricaoServico() { return descricaoServico; }
    public void setDescricaoServico(String descricaoServico) { this.descricaoServico = descricaoServico; }

    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; }
    public void setDataSolicitacao(LocalDateTime dataSolicitacao) { this.dataSolicitacao = dataSolicitacao; }

    public LocalDateTime getDataResposta() { return dataResposta; }
    public void setDataResposta(LocalDateTime dataResposta) { this.dataResposta = dataResposta; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}