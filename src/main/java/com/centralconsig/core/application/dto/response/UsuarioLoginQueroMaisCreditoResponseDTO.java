package com.centralconsig.core.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginQueroMaisCreditoResponseDTO {

    private String login;
    private String senha;
    private boolean somenteConsulta;

}
