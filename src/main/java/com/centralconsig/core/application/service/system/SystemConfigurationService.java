package com.centralconsig.core.application.service.system;

import com.centralconsig.core.domain.entity.SystemConfiguration;
import com.centralconsig.core.domain.repository.SystemConfigurationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigurationService {

    SystemConfigurationRepository systemConfigurationRepository;

    public SystemConfigurationService(SystemConfigurationRepository systemConfigurationRepository) {
        this.systemConfigurationRepository = systemConfigurationRepository;
    }

    @PostConstruct
    public void init() {
        if (systemConfigurationRepository.count() == 0L) {
            SystemConfiguration config = new SystemConfiguration();
            config.setPropostaAutomatica(false);
            config.setPropostaAutomaticaPlanilha(false);
            systemConfigurationRepository.save(config);
        } else if (systemConfigurationRepository.count() > 1L) {
            systemConfigurationRepository.deleteAll(systemConfigurationRepository.findAll().subList(1, systemConfigurationRepository.findAll().size()));
        }
    }

    public void atualizaValorPropostaAutomatica() {
        SystemConfiguration config = systemConfigurationRepository.findAll().getFirst();
        boolean ativando = !config.isPropostaAutomatica();
        config.setPropostaAutomatica(ativando);

        if (ativando && config.isPropostaAutomaticaPlanilha()) {
            config.setPropostaAutomaticaPlanilha(false);
        }

        systemConfigurationRepository.save(config);
    }

    public void atualizaValorPropostaAutomaticaPlanilha() {
        SystemConfiguration config = systemConfigurationRepository.findAll().getFirst();
        boolean ativando = !config.isPropostaAutomaticaPlanilha();
        config.setPropostaAutomaticaPlanilha(ativando);

        if (ativando && config.isPropostaAutomatica()) {
            config.setPropostaAutomatica(false);
        }

        systemConfigurationRepository.save(config);
    }

    public boolean isPropostaAutomaticaAtiva() {
        return systemConfigurationRepository.findAll().getFirst().isPropostaAutomatica();
    }

    public boolean isPropostaAutomaticaPlanilhaAtiva() {
        return systemConfigurationRepository.findAll().getFirst().isPropostaAutomaticaPlanilha();
    }
}