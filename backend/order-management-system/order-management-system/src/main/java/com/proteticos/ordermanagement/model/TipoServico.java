package com.proteticos.ordermanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.IOException;

// Opção com anotações no próprio enum
public enum TipoServico {
    COROA("coroa"),
    PONTE_FIXA("ponte"),  // Mapeia "ponte" para PONTE_FIXA
    PROVISORIO("provisorio"),
    PROTESE_TOTAL("protese_total"),
    PROTESE_PARCIAL("protese_parcial"),
    ZIRCONIA("zirconia"),
    RESINA("resina"),
    IMPLANTE("implante"),
    ORTODONTIA("ortodontia"),
    OUTRO("outro");

    private final String valorJson;

    TipoServico(String valorJson) {
        this.valorJson = valorJson;
    }

    @JsonValue
    public String getValorJson() {
        return valorJson;
    }

    @JsonCreator
    public static TipoServico fromValue(String value) {
        if (value == null) return null;

        // Normaliza: remove espaços, acentos, converte para minúsculo
        String normalized = value.trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("-", "_");

        // Mapeamento especial para "ponte"
        if (normalized.equals("ponte")) {
            return PONTE_FIXA;
        }

        for (TipoServico tipo : TipoServico.values()) {
            if (tipo.valorJson.equalsIgnoreCase(normalized) ||
                    tipo.name().equalsIgnoreCase(normalized)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException("Tipo de serviço desconhecido: " + value);
    }
}