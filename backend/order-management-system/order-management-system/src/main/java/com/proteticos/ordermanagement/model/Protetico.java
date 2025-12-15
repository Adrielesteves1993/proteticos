package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proteticos")
@PrimaryKeyJoinColumn(name = "usuario_id")  // IMPORTANTE para herança JOINED
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Protetico extends Usuario {  // ← AGORA ESTENDE USUARIO!

    // ============ CAMPOS ESPECÍFICOS DO PROTÉTICO ============
    private String telefone;

    @Column(name = "registro_profissional", unique = true)
    private String registroProfissional;

    private String especializacao;

    // ============ CAMPOS PARA TERCEIRIZAÇÃO ============
    @Column(name = "aceita_terceirizacao")
    private boolean aceitaTerceirizacao = false;

    @Column(name = "taxa_minima_terceirizacao", precision = 5, scale = 2)
    private BigDecimal taxaMinimaTerceirizacao = BigDecimal.valueOf(30.00);

    @Column(name = "nota_terceirizacao", precision = 3, scale = 2)
    private BigDecimal notaTerceirizacao = BigDecimal.ZERO;

    @Column(name = "quantidade_terceirizacoes")
    private Integer quantidadeTerceirizacoes = 0;

    @ElementCollection
    @CollectionTable(
            name = "protetico_especialidades_terceirizacao",
            joinColumns = @JoinColumn(name = "protetico_id")
    )
    @Column(name = "especialidade")
    @Enumerated(EnumType.STRING)
    private Set<TipoServico> especialidadesTerceirizacao = new HashSet<>();

    // ============ CONSTRUTORES ============
    public Protetico() {
        // Define tipo automaticamente como PROTETICO
        this.setTipo(UserTipo.PROTETICO);
    }

    // Construtor completo
    public Protetico(String nome, String email, String senha,
                     String registroProfissional, String especializacao) {
        // Chama construtor do pai (Usuario)
        super(nome, email, senha, UserTipo.PROTETICO);
        this.registroProfissional = registroProfissional;
        this.especializacao = especializacao;
    }

    // ============ GETTERS E SETTERS ESPECÍFICOS ============
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public String getEspecializacao() { return especializacao; }
    public void setEspecializacao(String especializacao) {
        this.especializacao = especializacao;
    }

    // ============ GETTERS E SETTERS DA TERCEIRIZAÇÃO ============
    public boolean isAceitaTerceirizacao() { return aceitaTerceirizacao; }
    public void setAceitaTerceirizacao(boolean aceitaTerceirizacao) {
        this.aceitaTerceirizacao = aceitaTerceirizacao;
    }

    public BigDecimal getTaxaMinimaTerceirizacao() { return taxaMinimaTerceirizacao; }
    public void setTaxaMinimaTerceirizacao(BigDecimal taxaMinimaTerceirizacao) {
        this.taxaMinimaTerceirizacao = taxaMinimaTerceirizacao;
    }

    public BigDecimal getNotaTerceirizacao() { return notaTerceirizacao; }
    public void setNotaTerceirizacao(BigDecimal notaTerceirizacao) {
        this.notaTerceirizacao = notaTerceirizacao;
    }

    public Integer getQuantidadeTerceirizacoes() { return quantidadeTerceirizacoes; }
    public void setQuantidadeTerceirizacoes(Integer quantidadeTerceirizacoes) {
        this.quantidadeTerceirizacoes = quantidadeTerceirizacoes;
    }

    public Set<TipoServico> getEspecialidadesTerceirizacao() {
        return especialidadesTerceirizacao;
    }

    public void setEspecialidadesTerceirizacao(Set<TipoServico> especialidadesTerceirizacao) {
        this.especialidadesTerceirizacao = especialidadesTerceirizacao;
    }

    // ============ MÉTODOS AUXILIARES ============
    public void adicionarEspecialidade(TipoServico especialidade) {
        this.especialidadesTerceirizacao.add(especialidade);
    }

    public void removerEspecialidade(TipoServico especialidade) {
        this.especialidadesTerceirizacao.remove(especialidade);
    }

    public boolean aceitaTerceirizacaoPara(TipoServico tipoServico) {
        if (!aceitaTerceirizacao) {
            return false;
        }
        if (especialidadesTerceirizacao == null || especialidadesTerceirizacao.isEmpty()) {
            return true;
        }
        return especialidadesTerceirizacao.contains(tipoServico);
    }

    public void atualizarNotaTerceirizacao(BigDecimal novaNota) {
        if (quantidadeTerceirizacoes == 0) {
            this.notaTerceirizacao = novaNota;
            this.quantidadeTerceirizacoes = 1;
        } else {
            BigDecimal totalAtual = this.notaTerceirizacao.multiply(
                    BigDecimal.valueOf(quantidadeTerceirizacoes));
            BigDecimal novoTotal = totalAtual.add(novaNota);
            this.notaTerceirizacao = novoTotal.divide(
                    BigDecimal.valueOf(quantidadeTerceirizacoes + 1),
                    2, java.math.RoundingMode.HALF_UP);
            this.quantidadeTerceirizacoes++;
        }
    }

    @Override
    public String toString() {
        return "Protetico{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", registroProfissional='" + registroProfissional + '\'' +
                ", aceitaTerceirizacao=" + aceitaTerceirizacao +
                '}';
    }
}