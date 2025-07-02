package com.centralconsig.core.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClienteResponseDTO {

    private String cpf;
    private String nome;
    private boolean blackList;
    private String telefone;
    private List<VinculoResponseDTO> vinculos;

}
