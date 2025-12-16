package com.proteticos.ordermanagement.DTO;

import java.math.BigDecimal;

public class ProteticoSimplesDTO {
    private Long id;
    private String nome;
    private String email;
    private String registroProfissional;
    private String especializacao;
    private Boolean aceitaTerceirizacao;

    // ============ NOVOS CAMPOS PARA TERCEIRIZAÇÃO ============
    private BigDecimal notaTerceirizacao;
    private Integer quantidadeTerceirizacoes;
    private BigDecimal taxaMinimaTerceirizacao;

    public ProteticoSimplesDTO() {}

    // ============ GETTERS E SETTERS BÁSICOS ============
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

    public Boolean getAceitaTerceirizacao() { return aceitaTerceirizacao; }
    public void setAceitaTerceirizacao(Boolean aceitaTerceirizacao) { this.aceitaTerceirizacao = aceitaTerceirizacao; }

    // Para compatibilidade (método is... além de get...)
    public boolean isAceitaTerceirizacao() {
        return aceitaTerceirizacao != null && aceitaTerceirizacao;
    }

    // ============ GETTERS E SETTERS DOS NOVOS CAMPOS ============
    public BigDecimal getNotaTerceirizacao() { return notaTerceirizacao; }
    public void setNotaTerceirizacao(BigDecimal notaTerceirizacao) { this.notaTerceirizacao = notaTerceirizacao; }

    public Integer getQuantidadeTerceirizacoes() { return quantidadeTerceirizacoes; }
    public void setQuantidadeTerceirizacoes(Integer quantidadeTerceirizacoes) { this.quantidadeTerceirizacoes = quantidadeTerceirizacoes; }

    public BigDecimal getTaxaMinimaTerceirizacao() { return taxaMinimaTerceirizacao; }
    public void setTaxaMinimaTerceirizacao(BigDecimal taxaMinimaTerceirizacao) { this.taxaMinimaTerceirizacao = taxaMinimaTerceirizacao; }

    @Override
    public String toString() {
        return "ProteticoSimplesDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", especializacao='" + especializacao + '\'' +
                '}';
    }
}