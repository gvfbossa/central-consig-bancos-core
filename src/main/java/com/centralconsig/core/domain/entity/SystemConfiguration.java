package com.centralconsig.core.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "system_config", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class SystemConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "proposta_automatica")
    private boolean propostaAutomatica;

    @Column(name = "proposta_automatica_planilha")
    private boolean propostaAutomaticaPlanilha;

}
