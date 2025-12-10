// StatusPedido.java
package com.proteticos.ordermanagement.model;

public enum StatusPedido {
    RASCUNHO,              // Quando dentista está criando
    AGUARDANDO_APROVACAO,  // Quando dentista finalizou e enviou para protético
    APROVADO,              // Quando protético aprovou
    EM_PRODUCAO,           // Quando protético começou a produzir
    AGUARDANDO_MATERIAL,   // Esperando material chegar
    TERCEIRIZADO,          // Trabalho terceirizado
    PRONTO_ENTREGA,        // Pronto para entregar
    ENTREGUE,              // Foi entregue ao dentista
    FINALIZADO,            // Sinônimo de ENTREGUE
    CONCLUIDO,             // Outro sinônimo
    CANCELADO;             // Cancelado

    // Método para verificar se está em aprovação
    public boolean isAguardandoAprovacao() {
        return this == AGUARDANDO_APROVACAO;
    }

    // Método para verificar se está em produção
    public boolean isEmProducao() {
        return this == EM_PRODUCAO || this == APROVADO;
    }

    // Método para verificar se está finalizado
    public boolean isFinalizado() {
        return this == ENTREGUE || this == FINALIZADO || this == CONCLUIDO;
    }
}