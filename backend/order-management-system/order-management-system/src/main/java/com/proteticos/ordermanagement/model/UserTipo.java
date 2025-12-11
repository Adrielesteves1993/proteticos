package com.proteticos.ordermanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserTipo {
    ADMIN("admin"),
    PROTETICO("protetico"),
    DENTISTA("dentista");  // ← Só esses 3 tipos

    private final String valor;

    UserTipo(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @JsonCreator
    public static UserTipo fromValue(String value) {
        if (value == null) return null;

        String normalized = value.trim().toLowerCase();

        for (UserTipo tipo : UserTipo.values()) {
            if (tipo.valor.equalsIgnoreCase(normalized) ||
                    tipo.name().equalsIgnoreCase(normalized)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de usuário desconhecido: " + value);
    }
}