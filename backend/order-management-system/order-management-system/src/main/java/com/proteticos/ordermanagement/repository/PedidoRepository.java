package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Pedido;
import com.proteticos.ordermanagement.model.StatusPedido;
import com.proteticos.ordermanagement.model.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // CONSULTA PERSONALIZADA COM JOIN FETCH
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

    // Buscar pedidos por protético (titular)
    List<Pedido> findByProteticoId(Long proteticoId);

    // ✅ NOVO: Buscar pedidos por protético terceirizado
    List<Pedido> findByProteticoTerceirizadoId(Long proteticoTerceirizadoId);

    // ✅ CORREÇÃO: Mudar para statusPedido
    List<Pedido> findByStatusPedido(StatusPedido statusPedido);

    // Buscar pedidos por tipo de serviço
    List<Pedido> findByTipoServico(TipoServico tipoServico);

    // ✅ CORREÇÃO: Mudar para statusPedido
    List<Pedido> findByDataPrevistaEntregaBeforeAndStatusPedidoNot(
            LocalDate data,
            StatusPedido statusPedido);

    // ✅ CORREÇÃO: Mudar para statusPedido
    List<Pedido> findByDataPrevistaEntregaAndStatusPedido(
            LocalDate data,
            StatusPedido statusPedido);

    // ✅ NOVO: Buscar pedidos com terceirização
    @Query("SELECT p FROM Pedido p WHERE p.statusTerceirizacao IS NOT NULL AND p.statusTerceirizacao != 'NAO_TERCEIRIZADO'")
    List<Pedido> findComTerceirizacao();

    // ✅ NOVO: Buscar pedidos por status de terceirização
    List<Pedido> findByStatusTerceirizacao(com.proteticos.ordermanagement.model.StatusTerceirizacao status);
}