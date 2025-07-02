package com.centralconsig.core.application.service;

import com.centralconsig.core.domain.entity.Vinculo;
import com.centralconsig.core.domain.repository.VinculoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VinculoService {

    private final VinculoRepository vinculoRepository;

    public VinculoService(VinculoRepository vinculoRepository) {
        this.vinculoRepository = vinculoRepository;
    }

    public Optional<Vinculo> findByMatriculaPensionista(String matricula) {
        return vinculoRepository.findByMatriculaPensionista(matricula);
    }
}
