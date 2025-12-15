package com.proteticos.ordermanagement.model;

/**
 * Status possíveis para um processo de terceirização
 *
 * Fluxo: NAO_TERCEIRIZADO → SOLICITADO → ACEITO → EM_ANDAMENTO → CONCLUIDO
 *        ou SOLICITADO → RECUSADO/CANCELADO
 */
public enum StatusTerceirizacao {

    // Estado inicial - pedido normal sem terceirização
    NAO_TERCEIRIZADO("Não Terceirizado"),

    // Protético titular solicitou terceirização
    SOLICITADO("Solicitado"),

    // Protético terceirizado aceitou o trabalho
    ACEITO("Aceito"),

    // Terceirização em execução
    EM_ANDAMENTO("Em Andamento"),

    // Parte terceirizada concluída
    CONCLUIDO("Concluído"),

    // Protético terceirizado recusou
    RECUSADO("Recusado"),

    // Terceirização cancelada (pelo solicitante)
    CANCELADO("Cancelado");

    private final String descricao;

    StatusTerceirizacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se a terceirização está ativa (não finalizada)
     */
    public boolean isAtivo() {
        return this == SOLICITADO || this == ACEITO || this == EM_ANDAMENTO;
    }

    /**
     * Verifica se a terceirização está finalizada
     */
    public boolean isFinalizado() {
        return this == CONCLUIDO || this == RECUSADO || this == CANCELADO;
    }

    /**
     * Verifica se pode mudar para outro status
     */
    public boolean podeMudarPara(StatusTerceirizacao novoStatus) {
        // Não pode mudar se já estiver finalizado
        if (this.isFinalizado()) {
            return false;
        }

        // Fluxo permitido:
        switch (this) {
            case NAO_TERCEIRIZADO:
                return novoStatus == SOLICITADO;
            case SOLICITADO:
                return novoStatus == ACEITO ||
                        novoStatus == RECUSADO ||
                        novoStatus == CANCELADO;
            case ACEITO:
                return novoStatus == EM_ANDAMENTO ||
                        novoStatus == CANCELADO;
            case EM_ANDAMENTO:
                return novoStatus == CONCLUIDO ||
                        novoStatus == CANCELADO;
            default:
                return false;
        }
    }
}