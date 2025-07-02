package com.centralconsig.core.application.mapper;

import com.centralconsig.core.application.dto.response.UsuarioLoginQueroMaisCreditoResponseDTO;
import com.centralconsig.core.domain.entity.UsuarioLoginQueroMaisCredito;

public class UsuarioMapper {
    
    public static UsuarioLoginQueroMaisCreditoResponseDTO toDto(UsuarioLoginQueroMaisCredito usuarioLoginQueroMaisCredito) {
        UsuarioLoginQueroMaisCreditoResponseDTO dto = new UsuarioLoginQueroMaisCreditoResponseDTO();
        dto.setLogin(usuarioLoginQueroMaisCredito.getUsername());
        dto.setSenha(usuarioLoginQueroMaisCredito.getPassword());
        dto.setSomenteConsulta(usuarioLoginQueroMaisCredito.isSomenteConsulta());
        
        return dto;
    }

}
