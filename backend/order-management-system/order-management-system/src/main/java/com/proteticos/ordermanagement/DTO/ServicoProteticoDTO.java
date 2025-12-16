package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.PoliticaExecucaoServico;
import com.proteticos.ordermanagement.model.TipoServico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicoProteticoDTO {
    private Long id;
    private TipoServico tipoServico;
    private String nomeServico;
    private BigDecimal preco;
    private String descricao;
    private Integer tempoMedioHoras;
    private Integer tempoMedioDias;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long proteticoId;
    private String proteticoNome;

    // NOVOS CAMPOS PARA TERCEIRIZAÇÃO
    private PoliticaExecucaoServico politicaExecucao;
    private BigDecimal precoTerceirizado;
    private Integer prazoTerceirizadoHoras;
    private Integer prazoTerceirizadoDias;
    private Long terceirizadoPreferidoId;
    private String observacoesTerceirizacao;
    private String terceirizadoPreferidoNome;

    // Construtores
    public ServicoProteticoDTO() {}

    public ServicoProteticoDTO(TipoServico tipoServico, BigDecimal preco) {
        this.tipoServico = tipoServico;
        this.preco = preco;
        this.nomeServico = tipoServico != null ? tipoServico.getNomeExibicao() : null;
    }

    // Método fromEntity atualizado
    public static ServicoProteticoDTO fromEntity(com.proteticos.ordermanagement.model.ServicoProtetico servico) {
        ServicoProteticoDTO dto = new ServicoProteticoDTO();
        dto.setId(servico.getId());
        dto.setTipoServico(servico.getTipoServico());
        dto.setPreco(servico.getPreco());
        dto.setDescricao(servico.getDescricao());
        dto.setAtivo(servico.isAtivo());
        dto.setDataCriacao(servico.getDataCriacao());
        dto.setDataAtualizacao(servico.getDataAtualizacao());

        // Copia protético info
        if (servico.getProtetico() != null) {
            dto.setProteticoId(servico.getProtetico().getId());
            dto.setProteticoNome(servico.getProtetico().getNome());
        }

        // Converte horas para dias
        if (servico.getTempoMedioHoras() != null) {
            dto.setTempoMedioHoras(servico.getTempoMedioHoras());
        }

        // NOVO: Campos de terceirização
        dto.setPoliticaExecucao(servico.getPoliticaExecucao());
        dto.setPrecoTerceirizado(servico.getPrecoTerceirizado());
        dto.setObservacoesTerceirizacao(servico.getObservacoesTerceirizacao());
        dto.setTerceirizadoPreferidoId(servico.getTerceirizadoPreferidoId());

        // Converte prazos de terceirização
        if (servico.getPrazoTerceirizadoHoras() != null) {
            dto.setPrazoTerceirizadoHoras(servico.getPrazoTerceirizadoHoras());
            dto.setPrazoTerceirizadoDias(servico.getPrazoTerceirizadoDias());
        }

        return dto;
    }

    // ============ GETTERS E SETTERS COMPLETOS ============

    // GETTERS E SETTERS BÁSICOS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoServico getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
        if (tipoServico != null) {
            this.nomeServico = tipoServico.getNomeExibicao();
        }
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    // GETTER/SETTER para horas
    public Integer getTempoMedioHoras() {
        return tempoMedioHoras;
    }

    public void setTempoMedioHoras(Integer tempoMedioHoras) {
        this.tempoMedioHoras = tempoMedioHoras;
        // Atualiza automaticamente os dias quando setar horas
        if (tempoMedioHoras != null) {
            this.tempoMedioDias = (int) Math.ceil(tempoMedioHoras / 24.0);
        } else {
            this.tempoMedioDias = null;
        }
    }

    // GETTER/SETTER para dias
    public Integer getTempoMedioDias() {
        if (tempoMedioDias != null) {
            return tempoMedioDias;
        }
        // Calcula dias a partir das horas se necessário
        return tempoMedioHoras != null ? (int) Math.ceil(tempoMedioHoras / 24.0) : null;
    }

    public void setTempoMedioDias(Integer tempoMedioDias) {
        this.tempoMedioDias = tempoMedioDias;
        // Atualiza automaticamente as horas quando setar dias
        if (tempoMedioDias != null) {
            this.tempoMedioHoras = tempoMedioDias * 24;
        } else {
            this.tempoMedioHoras = null;
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public Long getProteticoId() {
        return proteticoId;
    }

    public void setProteticoId(Long proteticoId) {
        this.proteticoId = proteticoId;
    }

    public String getProteticoNome() {
        return proteticoNome;
    }

    public void setProteticoNome(String proteticoNome) {
        this.proteticoNome = proteticoNome;
    }

    // ============ GETTERS E SETTERS PARA TERCEIRIZAÇÃO ============

    public PoliticaExecucaoServico getPoliticaExecucao() {
        return politicaExecucao;
    }

    public void setPoliticaExecucao(PoliticaExecucaoServico politicaExecucao) {
        this.politicaExecucao = politicaExecucao;
    }

    public BigDecimal getPrecoTerceirizado() {
        return precoTerceirizado;
    }

    public void setPrecoTerceirizado(BigDecimal precoTerceirizado) {
        this.precoTerceirizado = precoTerceirizado;
    }

    public Integer getPrazoTerceirizadoHoras() {
        return prazoTerceirizadoHoras;
    }

    public void setPrazoTerceirizadoHoras(Integer prazoTerceirizadoHoras) {
        this.prazoTerceirizadoHoras = prazoTerceirizadoHoras;
        // Atualiza automaticamente os dias quando setar horas
        if (prazoTerceirizadoHoras != null) {
            this.prazoTerceirizadoDias = (int) Math.ceil(prazoTerceirizadoHoras / 24.0);
        } else {
            this.prazoTerceirizadoDias = null;
        }
    }

    public Integer getPrazoTerceirizadoDias() {
        if (prazoTerceirizadoDias != null) {
            return prazoTerceirizadoDias;
        }
        // Calcula dias a partir das horas se necessário
        return prazoTerceirizadoHoras != null ?
                (int) Math.ceil(prazoTerceirizadoHoras / 24.0) : null;
    }

    public void setPrazoTerceirizadoDias(Integer prazoTerceirizadoDias) {
        this.prazoTerceirizadoDias = prazoTerceirizadoDias;
        // Atualiza automaticamente as horas quando setar dias
        if (prazoTerceirizadoDias != null) {
            this.prazoTerceirizadoHoras = prazoTerceirizadoDias * 24;
        } else {
            this.prazoTerceirizadoHoras = null;
        }
    }

    public Long getTerceirizadoPreferidoId() {
        return terceirizadoPreferidoId;
    }

    public void setTerceirizadoPreferidoId(Long terceirizadoPreferidoId) {
        this.terceirizadoPreferidoId = terceirizadoPreferidoId;
    }

    public String getObservacoesTerceirizacao() {
        return observacoesTerceirizacao;
    }

    public void setObservacoesTerceirizacao(String observacoesTerceirizacao) {
        this.observacoesTerceirizacao = observacoesTerceirizacao;
    }

    public String getTerceirizadoPreferidoNome() {
        return terceirizadoPreferidoNome;
    }

    public void setTerceirizadoPreferidoNome(String terceirizadoPreferidoNome) {
        this.terceirizadoPreferidoNome = terceirizadoPreferidoNome;
    }
}