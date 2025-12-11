// src/main/java/com/proteticos/ordermanagement/model/TipoServico.java
package com.proteticos.ordermanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum TipoServico {
    COROA("coroa"),
    PONTE_FIXA("ponte_fixa"),
    PROVISORIO("provisorio"),
    PROTESE_TOTAL("protese_total"),
    PROTESE_PARCIAL("protese_parcial"),
    ZIRCONIA("zirconia"),
    RESINA("resina"),
    IMPLANTE("implante"),
    ORTODONTIA("ortodontia"),
    OUTRO("outro");

    private final String valorJson;
    private static final Map<String, TipoServico> MAPA_VALORES = Arrays.stream(values())
            .collect(Collectors.toMap(TipoServico::getValorJson, Function.identity()));

    TipoServico(String valorJson) {
        this.valorJson = valorJson;
    }

    @JsonValue
    public String getValorJson() {
        return valorJson;
    }

    public String getNomeExibicao() {
        String nome = this.name().toLowerCase().replace("_", " ");
        return nome.substring(0, 1).toUpperCase() + nome.substring(1);
    }

    @JsonCreator
    public static TipoServico fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String normalized = value.trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("-", "_");

        // Mapeamento de sinônimos
        if (normalized.equals("ponte")) {
            return PONTE_FIXA;
        }

        return MAPA_VALORES.getOrDefault(normalized, OUTRO);
    }
}