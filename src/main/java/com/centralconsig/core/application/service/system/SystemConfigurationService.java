package com.centralconsig.core.application.service.system;

import com.centralconsig.core.application.dto.request.SystemConfigurationRequestDTO;
import com.centralconsig.core.application.dto.response.SystemConfigurationResponseDTO;
import com.centralconsig.core.application.mapper.SystemConfigurationMapper;
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

    public void atualizaValorPropostaAutomatica(SystemConfigurationRequestDTO responseDTO) {
        SystemConfiguration config = systemConfigurationRepository.findAll().getFirst();
        config.setPropostaAutomatica(responseDTO.isPropostaAutomatica());
        config.setPropostaAutomaticaPlanilha(responseDTO.isPropostaAutomaticaPlanilha());
        systemConfigurationRepository.save(config);
    }

    public SystemConfigurationResponseDTO getSystemConfigurations() {
        return SystemConfigurationMapper.toDto(systemConfigurationRepository.findAll().getFirst());
    }

}