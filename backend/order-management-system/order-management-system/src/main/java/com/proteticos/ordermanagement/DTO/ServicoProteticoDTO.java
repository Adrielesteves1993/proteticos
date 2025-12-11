// src/main/java/com/proteticos/ordermanagement/dto/ServicoProteticoDTO.java
package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.TipoServico;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServicoProteticoDTO {
    private Long id;
    private TipoServico tipoServico;
    private String nomeServico;
    private BigDecimal preco;
    private String descricao;
    private Integer tempoMedioHoras; // Mantém para compatibilidade interna
    private Integer tempoMedioDias;  // NOVO: campo para retornar ao frontend
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private Long proteticoId;
    private String proteticoNome;

    // Construtores
    public ServicoProteticoDTO() {}

    public ServicoProteticoDTO(TipoServico tipoServico, BigDecimal preco) {
        this.tipoServico = tipoServico;
        this.preco = preco;
        this.nomeServico = tipoServico != null ? tipoServico.getNomeExibicao() : null;
    }

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoServico getTipoServico() { return tipoServico; }
    public void setTipoServico(TipoServico tipoServico) {
        this.tipoServico = tipoServico;
        if (tipoServico != null) {
            this.nomeServico = tipoServico.getNomeExibicao();
        }
    }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // GETTER/SETTER para horas (mantido para compatibilidade interna)
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

    // GETTER/SETTER para dias (frontend usa este)
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

    // Método helper para converter de entidade para DTO
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
            dto.setTempoMedioHoras(servico.getTempoMedioHoras()); // Isso automaticamente seta os dias
        }

        return dto;
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public Long getProteticoId() { return proteticoId; }
    public void setProteticoId(Long proteticoId) { this.proteticoId = proteticoId; }

    public String getProteticoNome() { return proteticoNome; }
    public void setProteticoNome(String proteticoNome) { this.proteticoNome = proteticoNome; }
}