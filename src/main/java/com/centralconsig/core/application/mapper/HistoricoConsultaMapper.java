package com.centralconsig.core.application.mapper;

import com.centralconsig.core.application.dto.response.HistoricoConsultaResponseDTO;
import com.centralconsig.core.domain.entity.HistoricoConsulta;

public class HistoricoConsultaMapper {

    public static HistoricoConsultaResponseDTO toDto(HistoricoConsulta historico) {
        HistoricoConsultaResponseDTO dto = new HistoricoConsultaResponseDTO();
        dto.setMargemCredito(historico.getMargemCredito());
        dto.setAutorizacaoCredito(historico.getAutorizacaoCredito());
        dto.setSituacaoCredito(historico.getSituacaoCredito());

        dto.setMargemBeneficio(historico.getMargemBeneficio());
        dto.setAutorizacaoBeneficio(historico.getAutorizacaoBeneficio());
        dto.setSituacaoBeneficio(historico.getSituacaoBeneficio());

        dto.setDataConsulta(historico.getDataConsulta());
        return dto;
    }
}
