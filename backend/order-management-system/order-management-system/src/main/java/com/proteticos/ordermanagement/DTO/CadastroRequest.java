// controller/dto/CadastroRequest.java
package com.proteticos.ordermanagement.controller.dto;

import com.proteticos.ordermanagement.model.UserTipo;
import java.math.BigDecimal;

public class CadastroRequest {
    // Campos básicos (todos os tipos)
    private String nome;
    private String email;
    private String senha;
    private String codigoConvite;
    private UserTipo tipoUsuario;

    // Campos específicos de DENTISTA
    private String cro;
    private String especialidade;
    private String telefone;
    private String enderecoClinica;

    // Campos específicos de PROTÉTICO
    private String registroProfissional;
    private String especializacao;
    private Boolean aceitaTerceirizacao;
    private BigDecimal valorHora;
    private Integer capacidadePedidosSimultaneos;

    // Construtor padrão
    public CadastroRequest() {}

    // Construtor com campos básicos
    public CadastroRequest(String nome, String email, String senha, String codigoConvite, UserTipo tipoUsuario) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.codigoConvite = codigoConvite;
        this.tipoUsuario = tipoUsuario;
    }

    // === GETTERS E SETTERS ===

    // Campos básicos
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCodigoConvite() { return codigoConvite; }
    public void setCodigoConvite(String codigoConvite) { this.codigoConvite = codigoConvite; }

    public UserTipo getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(UserTipo tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    // Campos de DENTISTA
    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEnderecoClinica() { return enderecoClinica; }
    public void setEnderecoClinica(String enderecoClinica) { this.enderecoClinica = enderecoClinica; }

    // Campos de PROTÉTICO
    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getEspecializacao() { return especializacao; }
    public void setEspecializacao(String especializacao) { this.especializacao = especializacao; }

    public Boolean getAceitaTerceirizacao() { return aceitaTerceirizacao; }
    public void setAceitaTerceirizacao(Boolean aceitaTerceirizacao) { this.aceitaTerceirizacao = aceitaTerceirizacao; }

    public BigDecimal getValorHora() { return valorHora; }
    public void setValorHora(BigDecimal valorHora) { this.valorHora = valorHora; }

    public Integer getCapacidadePedidosSimultaneos() { return capacidadePedidosSimultaneos; }
    public void setCapacidadePedidosSimultaneos(Integer capacidadePedidosSimultaneos) {
        this.capacidadePedidosSimultaneos = capacidadePedidosSimultaneos;
    }

    // Método toString para debug
    @Override
    public String toString() {
        return "CadastroRequest{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                ", cro='" + cro + '\'' +
                ", registroProfissional='" + registroProfissional + '\'' +
                '}';
    }
}