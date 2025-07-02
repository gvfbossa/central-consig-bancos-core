package com.centralconsig.core.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "proposta")
@Getter
@Setter
public class Proposta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference
    private Cliente cliente;

    @Column(name = "numero_proposta")
    private String numeroProposta;

    @Column(name = "link_assinatura")
    private String linkAssinatura;

    @Column(name = "valor_liberado")
    private BigDecimal valorLiberado;

    @Column(name = "valor_parcela")
    private BigDecimal valorParcela;

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "processada")
    private boolean processada;

}
