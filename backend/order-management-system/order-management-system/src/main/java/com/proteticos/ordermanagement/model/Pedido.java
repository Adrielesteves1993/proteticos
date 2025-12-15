// src/main/java/com/proteticos/ordermanagement/model/Pedido.java
package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protetico_id", nullable = false)
    @JsonIgnoreProperties({"pedidos", "servicos", "senha"})
    private Protetico protetico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoServico tipoServico;

    private String informacoesDetalhadas;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorCobrado;

    private LocalDate dataEntrada;
    private LocalDate dataPrevistaEntrega;
    private LocalDate dataEntrega;
    private LocalDate dataCancelamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50)")
    private StatusPedido status = StatusPedido.AGUARDANDO_APROVACAO;

    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataUltimaAtualizacao;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("pedido")
    private List<EtapaPedido> etapas = new ArrayList<>();

    // Construtor padrão
    public Pedido() {}

    // Construtor com parâmetros principais
    public Pedido(Dentista dentista, Protetico protetico, TipoServico tipoServico) {
        this.dentista = dentista;
        this.protetico = protetico;
        this.tipoServico = tipoServico;
        this.dataEntrada = LocalDate.now();
    }

    // Gerar código automaticamente antes de salvar
    @PrePersist
    @PreUpdate
    public void gerarCodigoEAtualizar() {
        if (this.codigo == null) {
            this.codigo = "P" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        }
        if (this.dataEntrada == null) {
            this.dataEntrada = LocalDate.now();
        }
        this.dataUltimaAtualizacao = LocalDateTime.now();
    }

    // ============ GETTERS E SETTERS ============

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Dentista getDentista() {
        return dentista;
    }

    public void setDentista(Dentista dentista) {
        this.dentista = dentista;
    }

    public Protetico getProtetico() {
        return protetico;
    }

    public void setProtetico(Protetico protetico) {
        this.protetico = protetico;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
    }

    public String getInformacoesDetalhadas() {
        return informacoesDetalhadas;
    }

    public void setInformacoesDetalhadas(String informacoesDetalhadas) {
        this.informacoesDetalhadas = informacoesDetalhadas;
    }

    public BigDecimal getValorCobrado() {
        return valorCobrado;
    }

    public void setValorCobrado(BigDecimal valorCobrado) {
        this.valorCobrado = valorCobrado;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public void setDataPrevistaEntrega(LocalDate dataPrevistaEntrega) {
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public LocalDate getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(LocalDate dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataUltimaAtualizacao() {
        return dataUltimaAtualizacao;
    }

    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) {
        this.dataUltimaAtualizacao = dataUltimaAtualizacao;
    }

    public List<EtapaPedido> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<EtapaPedido> etapas) {
        this.etapas = etapas;
    }

    // ============ MÉTODOS AUXILIARES ============

    public boolean isFinalizado() {
        return status == StatusPedido.FINALIZADO;
    }

    public boolean isCancelado() {
        return status == StatusPedido.CANCELADO;
    }

    public boolean isEstadoFinal() {
        return isFinalizado() || isCancelado();
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", status=" + status +
                ", dataEntrada=" + dataEntrada +
                '}';
    }
}