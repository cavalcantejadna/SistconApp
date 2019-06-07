package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.Moradia;
import com.app.sistconApp.modelo.Pessoa;
import com.app.sistconApp.modelo.PessoaFisica;
import com.app.sistconApp.modelo.PessoaJuridica;
import com.app.sistconApp.modelo.Relacao;
import com.app.sistconApp.modelo.enums.TipoRelacao;
import com.app.sistconApp.repository.PessoaFisicaRepository;
import com.app.sistconApp.repository.PessoaJuridicaRepository;
import com.app.sistconApp.repository.PessoaRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
@Transactional
public class PessoaServiceImpl implements PessoaService {

    @Autowired
    private PessoaRepository pessoaRep;

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRep;

    @Autowired
    private PessoaJuridicaRepository pessoaJuridicaRep;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void salvar(Pessoa entidade) {
        if (entidade.getIdPessoa() == null) {
            padronizar(entidade);
            pessoaRep.save(entidade);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Pessoa ler(Long id) {
        return pessoaRep.findById(id).get();
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Pessoa> listar() {
        Condominio condominio = usuarioService.lerLogado().getCondominio();
        if (condominio == null) {
            return new ArrayList<>();
        }
        return condominio.getPessoas();
    }

    @Override
    public Page<Pessoa> listarPagina(Pageable pagina) {
        Condominio condominio = usuarioService.lerLogado().getCondominio();
        if (condominio == null) {
            return Page.empty(pagina);
        }
        return pessoaRep.findAllByCondominioOrderByNome(condominio, pagina);
    }

    @Override
    public void editar(Pessoa entidade) {
        padronizar(entidade);
        pessoaRep.save(entidade);
    }

    @Override
    public void excluir(Pessoa entidade) {
        pessoaRep.delete(entidade);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void validar(Pessoa entidade, BindingResult validacao) {
        // VALIDAÇÕES NA INCLUSÃO
        if (entidade.getIdPessoa() == null) {
            if (entidade instanceof PessoaFisica) {
                if (((PessoaFisica) entidade).getCpf() != null && pessoaFisicaRep.existsByCpfAndCondominio(
                        ((PessoaFisica) entidade).getCpf(), usuarioService.lerLogado().getCondominio())) {
                    validacao.rejectValue("cpf", "Unique");
                }
            } else if (entidade instanceof PessoaJuridica) {
                if (((PessoaJuridica) entidade).getCnpj() != null && pessoaJuridicaRep.existsByCnpjAndCondominio(
                        ((PessoaJuridica) entidade).getCnpj(), usuarioService.lerLogado().getCondominio())) {
                    validacao.rejectValue("cnpj", "Unique");
                }
            }
        } // VALIDAÇÕES NA ALTERAÇÃO
        else {
            if (entidade instanceof PessoaFisica) {
                if (((PessoaFisica) entidade).getCpf() != null
                        && pessoaFisicaRep.existsByCpfAndCondominioAndIdPessoaNot(((PessoaFisica) entidade).getCpf(),
                                usuarioService.lerLogado().getCondominio(), entidade.getIdPessoa())) {
                    validacao.rejectValue("cpf", "Unique");
                }
            } else if (entidade instanceof PessoaJuridica) {
                if (((PessoaJuridica) entidade).getCnpj() != null && pessoaJuridicaRep
                        .existsByCnpjAndCondominioAndIdPessoaNot(((PessoaJuridica) entidade).getCnpj(),
                                usuarioService.lerLogado().getCondominio(), entidade.getIdPessoa())) {
                    validacao.rejectValue("cnpj", "Unique");
                }
            }
        }
        // VALIDAÇÕES EM AMBOS
        // Validar relação
        List<Relacao> relacoes = entidade.getRelacoes();
        Set<Moradia> moradias = new HashSet<>();
        for (int i = 0; i < relacoes.size(); i++) {
            // Em uma relação é obrigatório ter a pessoa
            if (relacoes.get(i).getMoradia() == null) {
                validacao.rejectValue("relacoes[" + i + "].moradia", "NotNull");
            } else {
                if (moradias.contains(relacoes.get(i).getMoradia())) {
                    validacao.rejectValue("relacoes[" + i + "].moradia", "Unique");
                } else {
                    moradias.add(relacoes.get(i).getMoradia());
                }
            }
            // Se for proprietário é obrigatório ter o percentual
            if (TipoRelacao.P.equals(relacoes.get(i).getTipo())
                    && (relacoes.get(i).getParticipacaoDono() == null || relacoes.get(i).getParticipacaoDono() == 0)) {
                validacao.rejectValue("relacoes[" + i + "].participacaoDono", "NotNull");
            }
            // Se tiver data de saída não pode ser menor que a entrada
            if (relacoes.get(i).getDataEntrada() != null && relacoes.get(i).getDataSaida() != null
                    && relacoes.get(i).getDataSaida().isBefore(relacoes.get(i).getDataEntrada())) {
                validacao.rejectValue("relacoes[" + i + "].dataSaida", "typeMismatch");
            }
        }

    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void padronizar(Pessoa entidade) {
        if (entidade.getCondominio() == null) {
            entidade.setCondominio(usuarioService.lerLogado().getCondominio());
        }
        Iterator<Relacao> it = entidade.getRelacoes().iterator();
        while (it.hasNext()) {
            Relacao relacao = it.next();
            if (relacao.getPessoa() == null) {
                relacao.setPessoa(entidade);
            }
            if (relacao.getParticipacaoDono() == null) {
                relacao.setParticipacaoDono(new Float(0));
            }
        }

    }

}
