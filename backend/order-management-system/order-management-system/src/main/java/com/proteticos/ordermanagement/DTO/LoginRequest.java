// src/main/java/com/proteticos/ordermanagement/DTO/LoginRequest.java
package com.proteticos.ordermanagement.DTO;

import com.proteticos.ordermanagement.model.UserTipo;

public class LoginRequest {
    private String email;
    private String senha;
    private UserTipo tipo;

    public LoginRequest() {}

    public LoginRequest(String email, String senha, UserTipo tipo) {
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    // GETTERS E SETTERS
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public UserTipo getTipo() { return tipo; }
    public void setTipo(UserTipo tipo) { this.tipo = tipo; }
}