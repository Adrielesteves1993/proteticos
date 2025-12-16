// src/main/java/com/proteticos/ordermanagement/model/PoliticaExecucaoServico.java
package com.proteticos.ordermanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum PoliticaExecucaoServico {
    PROPRIO("proprio", "Execução própria"),
    TERCEIRIZADO("terceirizado", "Apenas terceirizado"),
    PROPRIO_OU_TERCEIRIZADO("proprio_ou_terceirizado", "Próprio ou terceirizado"),
    NAO_OFERECIDO("nao_oferecido", "Não oferecido");

    private final String valorJson;
    private final String descricao;
    private static final Map<String, PoliticaExecucaoServico> MAPA_VALORES =
            Arrays.stream(values())
                    .collect(Collectors.toMap(PoliticaExecucaoServico::getValorJson, Function.identity()));

    PoliticaExecucaoServico(String valorJson, String descricao) {
        this.valorJson = valorJson;
        this.descricao = descricao;
    }

    @JsonValue
    public String getValorJson() {
        return valorJson;
    }

    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static PoliticaExecucaoServico fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NAO_OFERECIDO;
        }

        String normalized = value.trim()
                .toLowerCase()
                .replace(" ", "_")
                .replace("-", "_");

        return MAPA_VALORES.getOrDefault(normalized, NAO_OFERECIDO);
    }
}