package com.centralconsig.core.domain.repository;

import com.centralconsig.core.domain.entity.UsuarioLoginQueroMaisCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioLoginQueroMaisCreditoRepository extends JpaRepository<UsuarioLoginQueroMaisCredito, Long> {
    Optional<UsuarioLoginQueroMaisCredito> findByUsernameAndPassword(String login, String senha);

    Optional<UsuarioLoginQueroMaisCredito> findByUsername(String login);
}
