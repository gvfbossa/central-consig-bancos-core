package com.centralconsig.core.application.mapper;

import com.centralconsig.core.application.dto.response.SystemConfigurationResponseDTO;
import com.centralconsig.core.domain.entity.SystemConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemConfigurationMapper {

    public static SystemConfigurationResponseDTO toDto(SystemConfiguration systemConfiguration) {
        SystemConfigurationResponseDTO dto = new SystemConfigurationResponseDTO();
        dto.setPropostaAutomatica(systemConfiguration.isPropostaAutomatica());
        dto.setPropostaAutomaticaPlanilha(systemConfiguration.isPropostaAutomaticaPlanilha());

        return dto;
    }
}
