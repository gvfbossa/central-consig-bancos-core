package com.centralconsig.core.application.service;

import com.centralconsig.core.domain.entity.Cliente;
import com.centralconsig.core.domain.entity.Proposta;
import com.centralconsig.core.domain.repository.PropostaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PropostaService {

    private final PropostaRepository propostaRepository;
    private final ClienteService clienteService;

    private static final Logger log = LoggerFactory.getLogger(PropostaService.class);

    public PropostaService(PropostaRepository propostaRepository, ClienteService clienteService) {
        this.propostaRepository = propostaRepository;
        this.clienteService = clienteService;
    }

    public void salvarPropostaEAtualizarCliente(Proposta proposta, Cliente cliente) {
        cliente = clienteService.salvarOuAtualizarCliente(cliente);
        Optional<Proposta> propostaExistenteOpt = propostaRepository.findByNumeroProposta(proposta.getNumeroProposta());
        Optional<List<Proposta>> proposCliente = propostaRepository.findByCliente(cliente);

        if (proposCliente.isPresent() && propostaExistenteOpt.isPresent()) {
            List<Proposta> deletarPropostas = new ArrayList<>();
            for (Proposta prop : proposCliente.get()) {
                if (!prop.getNumeroProposta().equals(propostaExistenteOpt.get().getNumeroProposta())) {
                    if (prop.getDataCadastro().equals(propostaExistenteOpt.get().getDataCadastro()))
                        deletarPropostas.add(prop);
                }
            }
            if (!deletarPropostas.isEmpty()) {
                List<String> numerosPropostasParaCancelar = deletarPropostas.stream()
                        .map(Proposta::getNumeroProposta)
                        .toList();
                //formularioCancelamentoPropostaService.cancelaPropostas(numerosPropostasParaCancelar);
                for (Proposta del : deletarPropostas) {
                    del.setCliente(null);
                    cliente.getPropostas().remove(del);
                    propostaRepository.delete(del);
                    log.info("Proposta " + del.getNumeroProposta() + " removida com sucesso.");
                }

                clienteService.salvarOuAtualizarCliente(cliente);
            }
        }

        Cliente clienteFinal = cliente;
        Proposta propostaFinal = propostaExistenteOpt.map(persistida -> {
            persistida.setCliente(clienteFinal);
            if (persistida.getDataCadastro() == null)
                persistida.setDataCadastro(proposta.getDataCadastro());
            persistida.setValorLiberado(proposta.getValorLiberado());
            persistida.setValorParcela(proposta.getValorParcela());
            persistida.setLinkAssinatura(proposta.getLinkAssinatura());
            return persistida;
        }).orElseGet(() -> {
            proposta.setCliente(clienteFinal);
            if (proposta.getDataCadastro() == null)
                proposta.setDataCadastro(LocalDate.now());
            return proposta;
        });
        propostaRepository.save(propostaFinal);
    }

    public Optional<Proposta> retornaPropostaPorNumero(String numero) {
        return propostaRepository.findByNumeroProposta(numero);
    }

    public Page<Proposta> getAllPropostas(Pageable pageable) {
        return propostaRepository.findAll(pageable);
    }

    public void removerProposta(String numeroProposta) {
        Optional<Proposta> propOpt = retornaPropostaPorNumero(numeroProposta);

        if (propOpt.isPresent()) {
            Proposta proposta = propOpt.get();
            Cliente cliente = proposta.getCliente();

            if (cliente != null) {
                cliente.getPropostas().remove(proposta);
                proposta.setCliente(null);
            }

            propostaRepository.delete(proposta);
            assert cliente != null;
            clienteService.salvarOuAtualizarCliente(cliente);
        }
    }

    public Optional<List<Proposta>> getPropostasPorCliente(Cliente cliente) {
        return propostaRepository.findByCliente(cliente);
    }

    public List<Proposta> todasAsPropostas() {
        return propostaRepository.findAll();
    }

    public List<Proposta> retornaPropostasPorFaltaDeInformacao() {
        return propostaRepository.getPropostasPorFaltaDeInformacao();
    }

    public List<Proposta> getTodasAsPropostasNaoProcessadas() {
        return propostaRepository.getTodasAsPropostasNaoProcessadas();
    }

    public void processaProposta(String numeroProposta) {
        Optional<Proposta> prop = propostaRepository.findByNumeroProposta(numeroProposta);
        if (prop.isPresent()) {
            prop.get().setProcessada(true);
            propostaRepository.save(prop.get());
        }
    }
}
