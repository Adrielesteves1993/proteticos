package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;  // ← ADICIONE ESTE IMPORT
@Entity
@Table(name = "proteticos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Protetico extends Usuario {
    private String registroProfissional;
    private String especializacao;
    private boolean aceitaTerceirizacao = true;
    private BigDecimal valorHora;
    private Integer capacidadePedidosSimultaneos = 5;

    // Construtor padrão (OBRIGATÓRIO)
    public Protetico() {}

    // Construtor completo
    public Protetico(String nome, String email, String senha, String registroProfissional, String especializacao) {
        super(nome, email, senha, UserTipo.PROTETICO);
        this.registroProfissional = registroProfissional;
        this.especializacao = especializacao;
    }

    // GETTERS E SETTERS
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