package com.proteticos.ordermanagement.repository;

import com.proteticos.ordermanagement.model.Usuario;
import com.proteticos.ordermanagement.model.UserTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Buscar por tipo de usuário
    List<Usuario> findByTipo(UserTipo tipo);

    // Verificar se email existe
    boolean existsByEmail(String email);

    // Buscar usuários ativos
    List<Usuario> findByAtivoTrue();
}