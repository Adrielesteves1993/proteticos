// src/main/java/com/proteticos/ordermanagement/DTO/LoginResponseDTO.java
package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.*;
import java.time.LocalDateTime;

public class LoginResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private UserTipo tipo;
    private boolean ativo;
    private LocalDateTime dataCriacao;

    // Campos específicos de protético
    private String registroProfissional;
    private String especializacao;
    // COMENTADO: Campo removido - agora a terceirização é por serviço
    // private Boolean aceitaTerceirizacao;

    // Campos específicos de dentista
    private String cro;
    private String especialidade;

    public LoginResponseDTO() {}

    public LoginResponseDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.tipo = usuario.getTipo();
        this.ativo = usuario.isAtivo();
        this.dataCriacao = usuario.getDataCriacao();

        if (usuario instanceof Protetico) {
            Protetico protetico = (Protetico) usuario;
            this.registroProfissional = protetico.getRegistroProfissional();
            this.especializacao = protetico.getEspecializacao();
            // COMENTADO: Não use mais este campo
            // this.aceitaTerceirizacao = protetico.isAceitaTerceirizacao();
        }

        if (usuario instanceof Dentista) {
            Dentista dentista = (Dentista) usuario;
            this.cro = dentista.getCro();
            this.especialidade = dentista.getEspecialidade();
        }
    }

    // GETTERS E SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserTipo getTipo() { return tipo; }
    public void setTipo(UserTipo tipo) { this.tipo = tipo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getRegistroProfissional() { return registroProfissional; }
    public void setRegistroProfissional(String registroProfissional) { this.registroProfissional = registroProfissional; }

    public String getEspecializacao() { return especializacao; }
    public void setEspecializacao(String especializacao) { this.especializacao = especializacao; }

    // COMENTADO: Getters e setters removidos
    /*
    public Boolean getAceitaTerceirizacao() { return aceitaTerceirizacao; }
    public void setAceitaTerceirizacao(Boolean aceitaTerceirizacao) { this.aceitaTerceirizacao = aceitaTerceirizacao; }
    */

    public String getCro() { return cro; }
    public void setCro(String cro) { this.cro = cro; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
}