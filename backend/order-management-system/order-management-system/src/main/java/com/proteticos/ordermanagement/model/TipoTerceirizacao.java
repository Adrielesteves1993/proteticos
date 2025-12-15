package com.proteticos.ordermanagement.model;

/**
 * Tipos de terceirização disponíveis
 */
public enum TipoTerceirizacao {

    COMPLETA("Completa", "Todo o serviço é terceirizado"),
    PARCIAL("Parcial", "Apenas parte do serviço é terceirizada"),
    ESPECIALIDADE("Especialidade", "Terceirização por falta de especialidade"),
    CAPACIDADE("Capacidade", "Terceirização por excesso de trabalho"),
    URGENCIA("Urgência", "Terceirização por questão de prazo");

    private final String nome;
    private final String descricao;

    TipoTerceirizacao(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o tipo a partir de uma string (case insensitive)
     */
    public static TipoTerceirizacao fromString(String texto) {
        if (texto == null) return PARCIAL;

        String textoNormalizado = texto.trim().toUpperCase();
        for (TipoTerceirizacao tipo : values()) {
            if (tipo.name().equals(textoNormalizado)) {
                return tipo;
            }
        }
        return PARCIAL; // Default
    }
}