// src/main/java/com/proteticos/ordermanagement/DTO/ProteticoSimplesDTO.java
package com.proteticos.ordermanagement.DTO;

public class ProteticoSimplesDTO {
    private Long id;
    private String nome;
    private String email;
    private String registroProfissional;
    private String especializacao;
    private Boolean aceitaTerceirizacao;

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

    public Boolean getAceitaTerceirizacao() { return aceitaTerceirizacao; }
    public void setAceitaTerceirizacao(Boolean aceitaTerceirizacao) { this.aceitaTerceirizacao = aceitaTerceirizacao; }
}