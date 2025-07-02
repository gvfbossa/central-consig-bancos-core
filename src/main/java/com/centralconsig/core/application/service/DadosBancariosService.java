package com.centralconsig.core.application.service;

import com.centralconsig.core.domain.entity.DadosBancarios;
import com.centralconsig.core.domain.repository.DadosBancariosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DadosBancariosService {

    private final DadosBancariosRepository dadosBancariosRepository;

    DadosBancariosService(DadosBancariosRepository dadosBancariosRepository) {
        this.dadosBancariosRepository = dadosBancariosRepository;
    }


    public DadosBancarios salvarDadosBancarios(DadosBancarios dados) {
        return dadosBancariosRepository.save(dados);
    }

    public List<DadosBancarios> getAllDados() {
        return dadosBancariosRepository.findAll();
    }

    public void removeDado(DadosBancarios dado) {
        dadosBancariosRepository.delete(dado);
    }

}
