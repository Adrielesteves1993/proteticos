package com.proteticos.ordermanagement.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;  // ← ADICIONE ESTE IMPORT
@Entity
@Table(name = "dentistas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Dentista extends Usuario {
    private String cro;
    private String especialidade;
    private String telefone;
    private String enderecoClinica;

    // Construtor padrão (OBRIGATÓRIO)
    public Dentista() {}

    // Construtor completo
    public Dentista(String nome, String email, String senha, String cro, String especialidade) {
        super(nome, email, senha, UserTipo.DENTISTA);
        this.cro = cro;
        this.especialidade = especialidade;
    }

    // GETTERS E SETTERS
    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEnderecoClinica() { return enderecoClinica; }
    public void setEnderecoClinica(String enderecoClinica) { this.enderecoClinica = enderecoClinica; }
}