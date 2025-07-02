package com.centralconsig.core.domain.repository;

import com.centralconsig.core.domain.entity.Vinculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VinculoRepository extends JpaRepository<Vinculo, Long> {
    Optional<Vinculo> findByMatriculaPensionista(String matricula);
}
