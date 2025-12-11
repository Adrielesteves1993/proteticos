// src/main/java/com/proteticos/ordermanagement/dto/ProteticoDTO.java
package com.proteticos.ordermanagement.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProteticoDTO {
    private Long id;
    private String nome;
    private String email;
    private String registroProfissional;
    private String especializacao;
    private boolean aceitaTerceirizacao;
    private BigDecimal valorHora;
    private Integer capacidadePedidosSimultaneos;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private List<ServicoProteticoDTO> servicos;

    // Construtores
    public ProteticoDTO() {}

    public ProteticoDTO(Long id, String nome, String email, String registroProfissional, String especializacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.registroProfissional = registroProfissional;
        this.especializacao = especializacao;
    }

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getEspecializacao() { return especializacao; }
    public void setEspecializacao(String especializacao) { this.especializacao = especializacao; }

    public boolean isAceitaTerceirizacao() { return aceitaTerceirizacao; }
    public void setAceitaTerceirizacao(boolean aceitaTerceirizacao) { this.aceitaTerceirizacao = aceitaTerceirizacao; }

    public BigDecimal getValorHora() { return valorHora; }
    public void setValorHora(BigDecimal valorHora) { this.valorHora = valorHora; }

    public Integer getCapacidadePedidosSimultaneos() { return capacidadePedidosSimultaneos; }
    public void setCapacidadePedidosSimultaneos(Integer capacidadePedidosSimultaneos) { this.capacidadePedidosSimultaneos = capacidadePedidosSimultaneos; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public List<ServicoProteticoDTO> getServicos() { return servicos; }
    public void setServicos(List<ServicoProteticoDTO> servicos) { this.servicos = servicos; }
}