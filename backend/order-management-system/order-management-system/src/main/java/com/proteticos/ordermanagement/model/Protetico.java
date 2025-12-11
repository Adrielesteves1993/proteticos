// src/main/java/com/proteticos/ordermanagement/model/Protetico.java
package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "proteticos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Protetico extends Usuario {

    @Column(name = "registro_profissional")
    private String registroProfissional;

    private String especializacao;

    @Column(name = "aceita_terceirizacao", columnDefinition = "boolean default true")
    private boolean aceitaTerceirizacao = true;

    @Column(name = "valor_hora", precision = 10, scale = 2)
    private BigDecimal valorHora;

    @Column(name = "capacidade_pedidos_simultaneos")
    private Integer capacidadePedidosSimultaneos = 5;

    @OneToMany(mappedBy = "protetico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ServicoProtetico> servicos = new ArrayList<>();

    public Protetico() {}

    public Protetico(String nome, String email, String senha, String registroProfissional, String especializacao) {
        super(nome, email, senha, UserTipo.PROTETICO);
        this.registroProfissional = registroProfissional;
        this.especializacao = especializacao;
    }

    // Métodos utilitários
    public void adicionarServico(ServicoProtetico servico) {
        servico.setProtetico(this);
        this.servicos.add(servico);
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

    public List<ServicoProtetico> getServicos() { return servicos; }
    public void setServicos(List<ServicoProtetico> servicos) { this.servicos = servicos; }
}