package com.centralconsig.core.application.service;

import com.centralconsig.core.application.dto.response.ClienteResponseDTO;
import com.centralconsig.core.application.mapper.ClienteMapper;
import com.centralconsig.core.domain.entity.Cliente;
import com.centralconsig.core.domain.entity.GoogleSheet;
import com.centralconsig.core.application.service.GoogleSheetService;
import com.centralconsig.core.domain.entity.HistoricoConsulta;
import com.centralconsig.core.domain.entity.Vinculo;
import com.centralconsig.core.domain.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final GoogleSheetService googleSheetService;
    private final VinculoService vinculoService;

    public ClienteService(ClienteRepository clienteRepository, VinculoService vinculoService, GoogleSheetService googleSheetService) {
        this.googleSheetService = googleSheetService;
        this.clienteRepository = clienteRepository;
        this.vinculoService = vinculoService;
    }

    public Cliente salvarOuAtualizarCliente(Cliente novoCliente) {
        Optional<Cliente> clienteExistenteOpt = clienteRepository.findByCpf(novoCliente.getCpf());

        if (clienteExistenteOpt.isPresent()) {
            Cliente existente = clienteExistenteOpt.get();

            if (!Objects.equals(existente.isCasa(), novoCliente.isCasa())) {
                existente.setCasa(novoCliente.isCasa());
            }

            for (Vinculo vinculoNovo : novoCliente.getVinculos()) {
                Optional<Vinculo> vinculoExistenteOpt = existente.getVinculos().stream()
                        .filter(v -> {
                            return v.getMatriculaPensionista().equals(vinculoNovo.getMatriculaPensionista())
                                    && Objects.equals(v.getOrgao(), vinculoNovo.getOrgao());
                        })
                        .findFirst();

                if (vinculoExistenteOpt.isPresent()) {
                    Vinculo vinculoExistente = vinculoExistenteOpt.get();

                    for (HistoricoConsulta historico : vinculoNovo.getHistoricos()) {
                        historico.setVinculo(vinculoExistente);
                        if (!vinculoExistente.getHistoricos().contains(historico)) {
                            vinculoExistente.getHistoricos().add(historico);

                            if (vinculoExistente.getHistoricos().size() > 2) {
                                vinculoExistente.getHistoricos().sort(Comparator.comparing(HistoricoConsulta::getDataConsulta));
                                vinculoExistente.getHistoricos().removeFirst();
                            }
                        }
                    }
                } else {
                    vinculoNovo.setCliente(existente);
                    existente.getVinculos().add(vinculoNovo);
                }
            }
            existente.setDadosBancarios(novoCliente.getDadosBancarios());
            existente.setNome(novoCliente.getNome());
            existente.setTelefone(novoCliente.getTelefone());
            existente.setCasa(novoCliente.isCasa());
            existente.setGoogleSheet(novoCliente.getGoogleSheet());
            return clienteRepository.saveAndFlush(existente);
        } else {
            return clienteRepository.saveAndFlush(novoCliente);
        }
    }

    public void salvarOuAtualizarEmLote(List<Cliente> clientesDoCsv) {
        Map<String, Cliente> mapaClientesExistentes = new HashMap<>();

        Set<String> cpfs = clientesDoCsv.stream()
                .map(Cliente::getCpf)
                .collect(Collectors.toSet());

        List<Cliente> clientesExistentes = clienteRepository.findByCpfIn(cpfs);

        for (Cliente existente : clientesExistentes) {
            mapaClientesExistentes.put(existente.getCpf(), existente);
        }

        List<Cliente> novos = new ArrayList<>();
        List<Cliente> atualizaveis = new ArrayList<>();

        for (Cliente clienteCsv : clientesDoCsv) {
            Cliente existente = mapaClientesExistentes.get(clienteCsv.getCpf());

            if (existente == null) {
                novos.add(clienteCsv);
            } else {
                boolean atualizado = false;

                if (!Objects.equals(existente.isCasa(), clienteCsv.isCasa())) {
                    existente.setCasa(clienteCsv.isCasa());
                    atualizado = true;
                }

                if (!Objects.equals(existente.getGoogleSheet(), clienteCsv.getGoogleSheet())) {
                    existente.setGoogleSheet(clienteCsv.getGoogleSheet());
                    atualizado = true;
                }

                for (Vinculo vinculoCsv : clienteCsv.getVinculos()) {
                    boolean existeVinculo = existente.getVinculos().stream()
                            .anyMatch(v -> v.getMatriculaPensionista().equals(vinculoCsv.getMatriculaPensionista())
                                    && Objects.equals(v.getOrgao(), vinculoCsv.getOrgao()));

                    if (!existeVinculo) {
                        vinculoCsv.setCliente(existente);
                        existente.getVinculos().add(vinculoCsv);
                        atualizado = true;
                    }
                }

                if (atualizado) {
                    atualizaveis.add(existente);
                }
            }
        }

        clienteRepository.saveAll(novos);
        clienteRepository.saveAll(atualizaveis);
    }

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    public List<Cliente> getAllClientesComHistoricoHoje() {
        return clienteRepository.buscarClientesComHistoricoConsultaHoje();
    }

    public Cliente criarObjetoCliente(String cpf, String nome, boolean casa, List<Vinculo> vinculos) {
        Cliente cliente = new Cliente();
        cliente.setCpf(cpf);
        cliente.setNome(nome);
        cliente.setVinculos(vinculos);
        cliente.setCasa(casa);
        return cliente;
    }

    public Cliente findByCpf(String cpf) {
        return clienteRepository.findByCpf(cpf).orElseThrow();
    }

    public String removeZerosAEsquerdaMatricula(String matricula) {
        return matricula.trim().replaceFirst("^0+(?!$)", "");
    }

    public ClienteResponseDTO buscaClientePorCpfOuMatricula(String cpf, String matricula) {
        Cliente cliente = null;

        if (cpf != null) {
            cliente = clienteRepository.findByCpf(cpf).orElse(null);
        } else if (matricula != null) {
            Vinculo vinculo = vinculoService.findByMatriculaPensionista(matricula)
                    .orElseThrow(() -> new IllegalArgumentException("Matrícula não encontrada."));
            cliente = vinculo.getCliente();
        } else {
            throw new IllegalArgumentException("Informe CPF ou Matrícula para a busca");
        }
        if (cliente != null) {
            return ClienteMapper.toDto(cliente);
        } else {
            return null;
        }
    }

    public List<Cliente> clientesFiltradosPorMargem() {
        return clienteRepository.findClientesElegiveisNative();
    }

    public List<Cliente> getClientesCasaComVinculosEHistorico() {
        List<Cliente> clientes = clienteRepository.buscarClientesCasaComVinculosEHistoricos();
        List<GoogleSheet> sheets = googleSheetService.getAllSheets();
        Map<Long, GoogleSheet> sheetsMap = sheets.stream().collect(Collectors.toMap(GoogleSheet::getId, Function.identity()));

        clientes.forEach(c -> {
            if (c.getGoogleSheet() != null) {
                GoogleSheet sheet = sheetsMap.get(c.getGoogleSheet().getId());
                c.setGoogleSheet(sheet);
            }
        });
        return clientes;
    }

    public List<Cliente> getClientesNaoCasaComVinculosEHistorico() {
        List<Cliente> clientes = clienteRepository.buscarClientesNaoCasaComVinculosEHistorico();
        List<GoogleSheet> sheets = googleSheetService.getAllSheets();
        Map<Long, GoogleSheet> sheetsMap = sheets.stream().collect(Collectors.toMap(GoogleSheet::getId, Function.identity()));

        clientes.forEach(c -> {
            if (c.getGoogleSheet() != null) {
                GoogleSheet sheet = sheetsMap.get(c.getGoogleSheet().getId());
                c.setGoogleSheet(sheet);
            }
        });
        return clientes;
    }

    public List<Cliente> getRelatorioMargensPreenchidasData(LocalDate data) {
        return clienteRepository.relatorioMargensPreenchidasData(data);
    }

    public List<Cliente> getClientesByDadosNull() {
        return clienteRepository.buscarClientesComDadosBancariosNull();
    }
}
