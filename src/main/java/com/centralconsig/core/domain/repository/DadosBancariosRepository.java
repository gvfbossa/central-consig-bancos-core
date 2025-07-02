package com.centralconsig.core.domain.repository;

import com.centralconsig.core.domain.entity.DadosBancarios;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DadosBancariosRepository extends JpaRepository<DadosBancarios, Long> {

    DadosBancarios findByClienteId(Long id);
}
