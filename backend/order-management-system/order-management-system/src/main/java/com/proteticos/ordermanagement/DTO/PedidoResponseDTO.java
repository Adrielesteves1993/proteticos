// src/main/java/com/proteticos/ordermanagement/DTO/PedidoResponseDTO.java
package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PedidoResponseDTO {
    private Long id;
    private String codigo;
    private DentistaSimplesDTO dentista;
    private ProteticoSimplesDTO protetico;
    private TipoServico tipoServico;
    private String informacoesDetalhadas;
    private BigDecimal valorCobrado;
    private LocalDate dataEntrada;
    private LocalDate dataPrevistaEntrega;
    private LocalDate dataEntrega;
    private StatusPedido status;
    private LocalDateTime dataCriacao;

    // Construtor vazio
    public PedidoResponseDTO() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public DentistaSimplesDTO getDentista() { return dentista; }
    public void setDentista(DentistaSimplesDTO dentista) { this.dentista = dentista; }

    public ProteticoSimplesDTO getProtetico() { return protetico; }
    public void setProtetico(ProteticoSimplesDTO protetico) { this.protetico = protetico; }

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

    public LocalDate getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDate dataEntrega) { this.dataEntrega = dataEntrega; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}