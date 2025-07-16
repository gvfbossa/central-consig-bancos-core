package com.centralconsig.core.domain.repository;

import com.centralconsig.core.domain.entity.Cliente;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);

    List<Cliente> findByCpfIn(Set<String> cpfs);

    @Query("""
    SELECT DISTINCT c
    FROM Cliente c
    LEFT JOIN FETCH c.googleSheet gs
    JOIN c.vinculos v
    LEFT JOIN v.historicos h
    WHERE c.casa = true
      AND (h.dataConsulta IS NULL OR h.dataConsulta <> CURRENT_DATE)
    """)
    List<Cliente> buscarClientesCasaComVinculosEHistoricos();

    @Query("""
    SELECT DISTINCT c
    FROM Cliente c
    LEFT JOIN FETCH c.googleSheet gs
    JOIN c.vinculos v
    LEFT JOIN v.historicos h
    WHERE c.casa = false
      AND (h.dataConsulta IS NULL OR h.dataConsulta <> CURRENT_DATE)
    """)
    List<Cliente> buscarClientesNaoCasaComVinculosEHistorico();

    @Query(value = """
    SELECT c.*
                FROM cliente c
                JOIN vinculo v ON c.id = v.cliente_id
                JOIN historico_consulta h ON v.id = h.vinculo_id
                WHERE v.orgao IS NOT NULL
                  AND v.orgao != ''
                  AND h.margem_beneficio IS NOT NULL
                  AND h.margem_beneficio != ''
                  AND h.margem_beneficio != '0,00'
                  AND h.situacao_beneficio = 'Autorizado'
                  AND h.data_consulta BETWEEN SUBDATE(CURDATE(), 1) and CURDATE()
                  AND CAST(REPLACE(h.margem_beneficio, ',', '.') AS DECIMAL) >= 30.0
                GROUP BY c.id
                ORDER BY MIN(CAST(REPLACE(h.margem_beneficio, ',', '.') AS DECIMAL))
    """, nativeQuery = true)
    List<Cliente> findClientesElegiveisNative();


    @Query(value = """
            SELECT c.*
                FROM cliente c
                JOIN vinculo v ON c.id = v.cliente_id
                JOIN historico_consulta h ON v.id = h.vinculo_id
                WHERE v.orgao IS NOT NULL
                  AND v.orgao != ''
                  AND h.margem_beneficio IS NOT NULL
                  AND h.margem_beneficio != ''
                  AND h.data_consulta = CURDATE()
                  AND CAST(REPLACE(h.margem_beneficio, ',', '.') AS DECIMAL) > 0
                GROUP BY c.id
    """, nativeQuery = true)
    List<Cliente> buscarClientesComHistoricoConsultaHoje();

    @Transactional
    @Query(value = """
            SELECT c.*
            FROM cliente c
            JOIN vinculo v ON c.id = v.cliente_id 
            AND v.orgao IS NOT NULL 
            JOIN historico_consulta hc ON v.id = hc.vinculo_id 
            AND hc.data_consulta between ? AND CURDATE()
            """, nativeQuery = true)
    List<Cliente> relatorioMargensPreenchidasData(LocalDate data);

    @Transactional
    @Query(value = """
            SELECT c.*
            FROM cliente c
            LEFT JOIN dados_bancarios db ON db.cliente_id = c.id
            WHERE db.id IS NULL
            """, nativeQuery = true)
    List<Cliente> buscarClientesComDadosBancariosNull();
}

