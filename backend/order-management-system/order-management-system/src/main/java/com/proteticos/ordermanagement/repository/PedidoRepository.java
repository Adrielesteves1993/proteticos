package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Pedido;
import com.proteticos.ordermanagement.model.StatusPedido;
import com.proteticos.ordermanagement.model.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;  // ← ADICIONE ESTE IMPORT
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // CONSULTA PERSONALIZADA COM JOIN FETCH - ADICIONE ESTE MÉTODO
    @Query("SELECT p FROM Pedido p JOIN FETCH p.dentista JOIN FETCH p.protetico")
    List<Pedido> findAllWithDetails();

    // Buscar pedido por código COM relacionamentos
    @EntityGraph(attributePaths = {"dentista", "protetico"})
    Optional<Pedido> findByCodigo(String codigo);

    // Buscar todos os pedidos COM relacionamentos
    @EntityGraph(attributePaths = {"dentista", "protetico"})
    List<Pedido> findAll();

    // Buscar pedidos por dentista
    List<Pedido> findByDentistaId(Long dentistaId);

    // Buscar pedidos por protético
    List<Pedido> findByProteticoId(Long proteticoId);

    // Buscar pedidos por status
    List<Pedido> findByStatus(StatusPedido status);

    // Buscar pedidos por tipo de serviço
    List<Pedido> findByTipoServico(TipoServico tipoServico);

    // Buscar pedidos atrasados
    List<Pedido> findByDataPrevistaEntregaBeforeAndStatusNot(LocalDate data, StatusPedido status);

    // Buscar pedidos para entrega hoje
    List<Pedido> findByDataPrevistaEntregaAndStatus(LocalDate data, StatusPedido status);
}