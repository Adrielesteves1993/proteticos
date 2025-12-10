package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "etapa_pedido")
public class EtapaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "nome_etapa", nullable = false)
    private String nomeEtapa;

    @Column(name = "observacoes", length = 1000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEtapa status;

    @Column(name = "ordem", nullable = false)
    private int ordem;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "responsavel_id")
    private Protetico responsavel;

    @Column(name = "prazo_estimado")
    private LocalDateTime prazoEstimado;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @Column(name = "data_prevista")
    private LocalDate dataPrevista;

    // ============ MÉTODOS DE CALLBACK ============

    @PrePersist
    protected void prePersist() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // ============ CONSTRUTORES ============

    public EtapaPedido() {
    }

    public EtapaPedido(Pedido pedido, String nomeEtapa, String observacoes,
                       StatusEtapa status, int ordem, LocalDateTime dataCriacao,
                       Protetico responsavel, LocalDate dataPrevista) {
        this.pedido = pedido;
        this.nomeEtapa = nomeEtapa;
        this.observacoes = observacoes;
        this.status = status;
        this.ordem = ordem;
        this.dataCriacao = dataCriacao != null ? dataCriacao : LocalDateTime.now();
        this.responsavel = responsavel;
        this.dataPrevista = dataPrevista;
    }

    // Construtor simplificado sem dataCriacao - usa a atual automaticamente
    public EtapaPedido(Pedido pedido, String nomeEtapa, String observacoes,
                       StatusEtapa status, int ordem, Protetico responsavel,
                       LocalDate dataPrevista) {
        this.pedido = pedido;
        this.nomeEtapa = nomeEtapa;
        this.observacoes = observacoes;
        this.status = status;
        this.ordem = ordem;
        this.dataCriacao = LocalDateTime.now(); // Define automaticamente
        this.responsavel = responsavel;
        this.dataPrevista = dataPrevista;
    }

    // ============ MÉTODOS DE AÇÃO ============

    public void concluir() {
        this.status = StatusEtapa.CONCLUIDA;
        this.dataConclusao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void concluir(String observacoesAdicionais) {
        this.concluir();
        if (observacoesAdicionais != null && !observacoesAdicionais.isEmpty()) {
            String novasObservacoes = this.observacoes == null ?
                    observacoesAdicionais :
                    this.observacoes + "\n" + observacoesAdicionais;
            this.setObservacoes(novasObservacoes);
        }
    }

    public void iniciar() {
        if (this.status == StatusEtapa.PENDENTE) {
            this.status = StatusEtapa.EM_ANDAMENTO;
            this.dataAtualizacao = LocalDateTime.now();
        }
    }

    public void cancelar() {
        this.status = StatusEtapa.CANCELADA;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public boolean isConcluida() {
        return StatusEtapa.CONCLUIDA.equals(this.status);
    }

    public boolean isPendente() {
        return StatusEtapa.PENDENTE.equals(this.status);
    }

    public boolean isEmAndamento() {
        return StatusEtapa.EM_ANDAMENTO.equals(this.status);
    }

    public boolean isCancelada() {
        return StatusEtapa.CANCELADA.equals(this.status);
    }

    // ============ GETTERS E SETTERS ============

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public String getNomeEtapa() {
        return nomeEtapa;
    }

    public void setNomeEtapa(String nomeEtapa) {
        this.nomeEtapa = nomeEtapa;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusEtapa getStatus() {
        return status;
    }

    public void setStatus(StatusEtapa status) {
        this.status = status;
        // Atualiza dataAtualizacao quando muda o status
        this.dataAtualizacao = LocalDateTime.now();
    }

    public int getOrdem() {
        return ordem;
    }

    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Protetico getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Protetico responsavel) {
        this.responsavel = responsavel;
        // Atualiza dataAtualizacao quando muda o responsável
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LocalDateTime getPrazoEstimado() {
        return prazoEstimado;
    }

    public void setPrazoEstimado(LocalDateTime prazoEstimado) {
        this.prazoEstimado = prazoEstimado;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public LocalDate getDataPrevista() {
        return dataPrevista;
    }

    public void setDataPrevista(LocalDate dataPrevista) {
        this.dataPrevista = dataPrevista;
    }

    // ============ MÉTODOS ADICIONAIS ============

    // Método factory para criar etapas iniciais
    public static EtapaPedido criarEtapaInicial(Pedido pedido, String nomeEtapa,
                                                String observacoes, int ordem) {
        EtapaPedido etapa = new EtapaPedido();
        etapa.setPedido(pedido);
        etapa.setNomeEtapa(nomeEtapa);
        etapa.setObservacoes(observacoes);
        etapa.setStatus(StatusEtapa.PENDENTE);
        etapa.setOrdem(ordem);
        // dataCriacao será automaticamente definida no @PrePersist
        return etapa;
    }

    // toString para debug
    @Override
    public String toString() {
        return "EtapaPedido{" +
                "id=" + id +
                ", pedido=" + (pedido != null ? pedido.getId() : "null") +
                ", nomeEtapa='" + nomeEtapa + '\'' +
                ", status=" + status +
                ", ordem=" + ordem +
                ", dataCriacao=" + dataCriacao +
                ", dataPrevista=" + dataPrevista +
                ", responsavel=" + (responsavel != null ? responsavel.getNome() : "null") +
                ", concluida=" + isConcluida() +
                '}';
    }

    // Métodos equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtapaPedido that = (EtapaPedido) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}