package com.proteticos.ordermanagement.dto;

import java.math.BigDecimal;

public class ProteticoSimplesDTO {
    private Long id;
    private String nome;
    private String email;
    private String registroProfissional;
    private String especializacao;
    private boolean aceitaTerceirizacao;
    private BigDecimal valorHora;
    private Integer capacidadePedidosSimultaneos;

    public ProteticoSimplesDTO() {}

    // Getters e Setters
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
}