// dto/GerarConviteRequest.java (opcional, mas organizado)
package com.proteticos.ordermanagement.dto;

import com.proteticos.ordermanagement.model.UserTipo;

public class GerarConviteRequest {
    private UserTipo tipo;
    private String emailConvidado;
    private Integer diasValidade = 7; // Opcional

    // getters e setters
    public UserTipo getTipo() { return tipo; }
    public void setTipo(UserTipo tipo) { this.tipo = tipo; }

    public String getEmailConvidado() { return emailConvidado; }
    public void setEmailConvidado(String emailConvidado) { this.emailConvidado = emailConvidado; }

    public Integer getDiasValidade() { return diasValidade; }
    public void setDiasValidade(Integer diasValidade) { this.diasValidade = diasValidade; }
}