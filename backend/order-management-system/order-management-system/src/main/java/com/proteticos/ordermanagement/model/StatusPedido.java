package com.proteticos.ordermanagement.model;

public enum StatusPedido {
    // Estado inicial quando dentista cria
    AGUARDANDO_APROVACAO,  // Quando dentista criou e enviou para protético
    APROVADO,              // Quando protético aprovou
    EM_PRODUCAO,           // Quando protético começou a produzir
    FINALIZADO,            // Trabalho concluído (pronto para entrega)
    CANCELADO;             // Cancelado (pode acontecer em qualquer momento)

    // VALIDAÇÃO DE TRANSIÇÕES - NÃO PERMITE VOLTAR ATRÁS
    public boolean podeMudarPara(StatusPedido novoStatus) {
        // Se for cancelar, sempre pode (exceto se já estiver cancelado ou finalizado)
        if (novoStatus == CANCELADO) {
            return this != CANCELADO && this != FINALIZADO;
        }

        // Não pode mudar para estados já finalizados
        if (this == FINALIZADO || this == CANCELADO) {
            return false;
        }

        // Fluxo normal (sequencial, não pode retroceder)
        switch (this) {
            case AGUARDANDO_APROVACAO:
                return novoStatus == APROVADO || novoStatus == CANCELADO;
            case APROVADO:
                return novoStatus == EM_PRODUCAO || novoStatus == CANCELADO;
            case EM_PRODUCAO:
                return novoStatus == FINALIZADO || novoStatus == CANCELADO;
            case FINALIZADO:
                return false; // Estado final
            case CANCELADO:
                return false; // Estado final
            default:
                return false;
        }
    }

    // Verifica se o status permite edição
    public boolean permiteEdicao() {
        return this == AGUARDANDO_APROVACAO || this == APROVADO;
    }

    // Verifica se está em um estado final
    public boolean isEstadoFinal() {
        return this == FINALIZADO || this == CANCELADO;
    }

    // Obtém próximos status possíveis
    public StatusPedido[] getProximosStatus() {
        switch (this) {
            case AGUARDANDO_APROVACAO:
                return new StatusPedido[]{APROVADO, CANCELADO};
            case APROVADO:
                return new StatusPedido[]{EM_PRODUCAO, CANCELADO};
            case EM_PRODUCAO:
                return new StatusPedido[]{FINALIZADO, CANCELADO};
            default:
                return new StatusPedido[]{};
        }
    }

    // Verifica se é um status "ativo" (não finalizado)
    public boolean isAtivo() {
        return this != FINALIZADO && this != CANCELADO;
    }
}