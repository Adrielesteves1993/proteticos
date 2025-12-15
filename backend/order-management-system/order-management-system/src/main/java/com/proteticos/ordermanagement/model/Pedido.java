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

    // ============ CAMPO STATUS DO PEDIDO (ORIGINAL) ============
    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false, columnDefinition = "VARCHAR(50)")
    private StatusPedido statusPedido = StatusPedido.AGUARDANDO_APROVACAO;

    private LocalDateTime dataCriacao = LocalDateTime.now();
    private LocalDateTime dataUltimaAtualizacao;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("pedido")
    private List<EtapaPedido> etapas = new ArrayList<>();

    // ============ NOVOS CAMPOS PARA TERCEIRIZAÇÃO ============
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protetico_terceirizado_id")
    @JsonIgnoreProperties({"pedidos", "servicos", "senha", "hibernateLazyInitializer"})
    private Protetico proteticoTerceirizado;

    @Column(name = "percentual_terceirizado", precision = 5, scale = 2)
    private BigDecimal percentualTerceirizado;

    @Column(name = "valor_terceirizado", precision = 10, scale = 2)
    private BigDecimal valorTerceirizado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_terceirizacao")
    private StatusTerceirizacao statusTerceirizacao = StatusTerceirizacao.NAO_TERCEIRIZADO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_terceirizacao")
    private TipoTerceirizacao tipoTerceirizacao;

    @Column(name = "motivo_terceirizacao", columnDefinition = "TEXT")
    private String motivoTerceirizacao;

    @Column(name = "data_solicitacao_terceirizacao")
    private LocalDateTime dataSolicitacaoTerceirizacao;

    @Column(name = "data_resposta_terceirizacao")
    private LocalDateTime dataRespostaTerceirizacao;

    @Column(name = "data_conclusao_terceirizacao")
    private LocalDateTime dataConclusaoTerceirizacao;

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

    // ============ GETTERS E SETTERS ORIGINAIS ============
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Dentista getDentista() { return dentista; }
    public void setDentista(Dentista dentista) { this.dentista = dentista; }

    public Protetico getProtetico() { return protetico; }
    public void setProtetico(Protetico protetico) { this.protetico = protetico; }

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

    public LocalDate getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDate dataCancelamento) { this.dataCancelamento = dataCancelamento; }

    // ============ GETTER/SETTER DO STATUS DO PEDIDO (CORRIGIDO) ============
    public StatusPedido getStatusPedido() {  // ← NOME DO MÉTODO CORRIGIDO
        return statusPedido;
    }

    public void setStatusPedido(StatusPedido statusPedido) {  // ← NOME DO MÉTODO CORRIGIDO
        this.statusPedido = statusPedido;
    }

    // Método getStatus() para compatibilidade (se precisar)
    public StatusPedido getStatus() {
        return statusPedido;
    }

    public void setStatus(StatusPedido status) {
        this.statusPedido = status;
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataUltimaAtualizacao() { return dataUltimaAtualizacao; }
    public void setDataUltimaAtualizacao(LocalDateTime dataUltimaAtualizacao) { this.dataUltimaAtualizacao = dataUltimaAtualizacao; }

    public List<EtapaPedido> getEtapas() { return etapas; }
    public void setEtapas(List<EtapaPedido> etapas) { this.etapas = etapas; }

    // ============ GETTERS E SETTERS DA TERCEIRIZAÇÃO ============
    public Protetico getProteticoTerceirizado() { return proteticoTerceirizado; }
    public void setProteticoTerceirizado(Protetico proteticoTerceirizado) { this.proteticoTerceirizado = proteticoTerceirizado; }

    public BigDecimal getPercentualTerceirizado() { return percentualTerceirizado; }
    public void setPercentualTerceirizado(BigDecimal percentualTerceirizado) { this.percentualTerceirizado = percentualTerceirizado; }

    public BigDecimal getValorTerceirizado() { return valorTerceirizado; }
    public void setValorTerceirizado(BigDecimal valorTerceirizado) { this.valorTerceirizado = valorTerceirizado; }

    public StatusTerceirizacao getStatusTerceirizacao() { return statusTerceirizacao; }
    public void setStatusTerceirizacao(StatusTerceirizacao statusTerceirizacao) { this.statusTerceirizacao = statusTerceirizacao; }

    public TipoTerceirizacao getTipoTerceirizacao() { return tipoTerceirizacao; }
    public void setTipoTerceirizacao(TipoTerceirizacao tipoTerceirizacao) { this.tipoTerceirizacao = tipoTerceirizacao; }

    public String getMotivoTerceirizacao() { return motivoTerceirizacao; }
    public void setMotivoTerceirizacao(String motivoTerceirizacao) { this.motivoTerceirizacao = motivoTerceirizacao; }

    public LocalDateTime getDataSolicitacaoTerceirizacao() { return dataSolicitacaoTerceirizacao; }
    public void setDataSolicitacaoTerceirizacao(LocalDateTime dataSolicitacaoTerceirizacao) { this.dataSolicitacaoTerceirizacao = dataSolicitacaoTerceirizacao; }

    public LocalDateTime getDataRespostaTerceirizacao() { return dataRespostaTerceirizacao; }
    public void setDataRespostaTerceirizacao(LocalDateTime dataRespostaTerceirizacao) { this.dataRespostaTerceirizacao = dataRespostaTerceirizacao; }

    public LocalDateTime getDataConclusaoTerceirizacao() { return dataConclusaoTerceirizacao; }
    public void setDataConclusaoTerceirizacao(LocalDateTime dataConclusaoTerceirizacao) { this.dataConclusaoTerceirizacao = dataConclusaoTerceirizacao; }

    // ============ MÉTODOS AUXILIARES ORIGINAIS ============
    public boolean isFinalizado() {
        return statusPedido == StatusPedido.FINALIZADO;
    }

    public boolean isCancelado() {
        return statusPedido == StatusPedido.CANCELADO;
    }

    public boolean isEstadoFinal() {
        return isFinalizado() || isCancelado();
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", statusPedido=" + statusPedido +  // ← ATUALIZADO
                ", dataEntrada=" + dataEntrada +
                '}';
    }

    // ============ MÉTODOS AUXILIARES DA TERCEIRIZAÇÃO ============

    /**
     * Verifica se o pedido está terceirizado
     */
    public boolean isTerceirizado() {
        return statusTerceirizacao != StatusTerceirizacao.NAO_TERCEIRIZADO;
    }

    /**
     * Verifica se a terceirização está ativa
     */
    public boolean isTerceirizacaoAtiva() {
        return statusTerceirizacao != null && statusTerceirizacao.isAtivo();
    }

    /**
     * Verifica se pode ser terceirizado
     */
    public boolean podeSerTerceirizado() {
        // Só pode terceirizar se:
        // 1. Não estiver finalizado ou cancelado
        // 2. Não estiver já terceirizado ou em processo
        // 3. Estiver em status apropriado (APROVADO ou EM_PRODUCAO)

        // USANDO O CAMPO CORRETO: statusPedido
        if (statusPedido.isEstadoFinal()) {
            return false;
        }

        if (this.isTerceirizado() && !statusTerceirizacao.isFinalizado()) {
            return false; // Já está em processo de terceirização
        }

        // Só permite terceirizar em certos status
        return statusPedido == StatusPedido.APROVADO ||
                statusPedido == StatusPedido.EM_PRODUCAO;
    }

    /**
     * Solicita terceirização
     */
    public void solicitarTerceirizacao(Protetico proteticoTerceirizado,
                                       BigDecimal percentual,
                                       TipoTerceirizacao tipo,
                                       String motivo) {
        this.proteticoTerceirizado = proteticoTerceirizado;
        this.percentualTerceirizado = percentual;
        this.tipoTerceirizacao = tipo;
        this.motivoTerceirizacao = motivo;
        this.statusTerceirizacao = StatusTerceirizacao.SOLICITADO;
        this.dataSolicitacaoTerceirizacao = LocalDateTime.now();

        // Calcula valor terceirizado
        if (this.valorCobrado != null && percentual != null) {
            this.valorTerceirizado = this.valorCobrado
                    .multiply(percentual)
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    /**
     * Aceita terceirização
     */
    public void aceitarTerceirizacao() {
        this.statusTerceirizacao = StatusTerceirizacao.ACEITO;
        this.dataRespostaTerceirizacao = LocalDateTime.now();
    }

    /**
     * Recusa terceirização
     */
    public void recusarTerceirizacao() {
        this.statusTerceirizacao = StatusTerceirizacao.RECUSADO;
        this.dataRespostaTerceirizacao = LocalDateTime.now();
        this.proteticoTerceirizado = null; // Limpa o protético terceirizado
    }

    /**
     * Inicia a execução da terceirização
     */
    public void iniciarTerceirizacao() {
        this.statusTerceirizacao = StatusTerceirizacao.EM_ANDAMENTO;
    }

    /**
     * Conclui a terceirização
     */
    public void concluirTerceirizacao() {
        this.statusTerceirizacao = StatusTerceirizacao.CONCLUIDO;
        this.dataConclusaoTerceirizacao = LocalDateTime.now();
    }

    /**
     * Cancela a terceirização
     */
    public void cancelarTerceirizacao() {
        this.statusTerceirizacao = StatusTerceirizacao.CANCELADO;
        this.proteticoTerceirizado = null; // Limpa o protético terceirizado
    }
}