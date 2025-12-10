package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.EtapaPedido;
import com.proteticos.ordermanagement.model.StatusEtapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EtapaPedidoRepository extends JpaRepository<EtapaPedido, Long> {

    // Buscar etapas por pedido
    List<EtapaPedido> findByPedidoId(Long pedidoId);

    // Buscar etapas por responsável
    List<EtapaPedido> findByResponsavelId(Long proteticoId);

    // Buscar etapas por status
    List<EtapaPedido> findByStatus(StatusEtapa status);

    // Buscar etapas atrasadas
    @Query("SELECT e FROM EtapaPedido e WHERE e.dataPrevista < CURRENT_DATE AND e.status != com.proteticos.ordermanagement.model.StatusEtapa.CONCLUIDA")
    List<EtapaPedido> findEtapasAtrasadas();

    // Buscar etapas para hoje
    List<EtapaPedido> findByDataPrevistaAndStatusNot(LocalDate data, StatusEtapa status);

    // Buscar etapas por pedido ordenadas por ordem
    List<EtapaPedido> findByPedidoIdOrderByOrdemAsc(Long pedidoId);

    // ✅ MÉTODO NOVO - Buscar etapa específica por pedido e ordem
    Optional<EtapaPedido> findByPedidoIdAndOrdem(Long pedidoId, Integer ordem);
}