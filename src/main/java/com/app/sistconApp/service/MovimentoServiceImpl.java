package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Categoria;
import com.app.sistconApp.modelo.Conta;
import com.app.sistconApp.modelo.Lancamento;
import com.app.sistconApp.modelo.Movimento;
import com.app.sistconApp.modelo.Periodo;
import com.app.sistconApp.modelo.Subcategoria;
import com.app.sistconApp.modelo.Transferencia;
import com.app.sistconApp.modelo.enums.TipoCategoria;
import com.app.sistconApp.repository.LancamentosRepository;
import com.app.sistconApp.repository.MovimentoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;


@Service
@Transactional
public class MovimentoServiceImpl implements MovimentoService {

	@Autowired
	private MovimentoRepository moviRep;

	@Autowired
	private LancamentosRepository lr;

	@Autowired
	private ContaService contaService;

	@Autowired
	private PeriodoService periodoService;

	@Override
	public void salvar(Movimento entidade) {
		if (entidade.getIdMovimento() == null) {
			padronizar(entidade);
			List<Movimento> listaSalvar = new ArrayList<>();
			Transferencia contrapartida;
			if (entidade instanceof Lancamento) {
				((Lancamento) entidade).setPeriodo(periodoService.ler(entidade.getData()));
				if (((Lancamento) entidade).getSubcategoria().getCategoriaPai().getTipo().equals(TipoCategoria.D)) {
					entidade.setReducao(Boolean.TRUE);
				} else {
					entidade.setReducao(Boolean.FALSE);
				}
			} else if (entidade instanceof Transferencia) {
				entidade.setReducao(Boolean.TRUE);
				// LATER ver se tem forma mais prática de criar espelho do movimento
				contrapartida = new Transferencia();
				contrapartida.setData(entidade.getData());
				contrapartida.setValor(entidade.getValor());
				contrapartida.setDocumento(entidade.getDocumento());
				contrapartida.setDescricao(entidade.getDescricao());
				contrapartida.setConta(((Transferencia) entidade).getContaInversa());
				contrapartida.setContaInversa(entidade.getConta());
				contrapartida.setReducao(Boolean.FALSE);
				contrapartida.setMovimentoInverso(entidade);
				((Transferencia) entidade).setMovimentoInverso(contrapartida);
				listaSalvar.add(contrapartida);
			}
			listaSalvar.add(entidade);
			moviRep.saveAll(listaSalvar);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Movimento ler(Long id) {
		return moviRep.findById(id).get();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Movimento> listar() {
		return moviRep.findAllByContaInOrderByDataDesc(contaService.listar());
	}

	@Override
	public Page<Movimento> listarPagina(Pageable pagina) {
		return moviRep.findAllByContaInOrderByDataDesc(contaService.listar(), pagina);
	}

	@Override
	public void editar(Movimento entidade) {
		padronizar(entidade);
		List<Movimento> listaSalvar = new ArrayList<>();
		if (entidade instanceof Lancamento) {
			((Lancamento) entidade).setPeriodo(periodoService.ler(entidade.getData()));
			if (((Lancamento) entidade).getSubcategoria().getCategoriaPai().getTipo().equals(TipoCategoria.D)) {
				entidade.setReducao(Boolean.TRUE);
			} else {
				entidade.setReducao(Boolean.FALSE);
			}
		} else if (entidade instanceof Transferencia) {
			((Transferencia) entidade).getMovimentoInverso().setData(entidade.getData());
			((Transferencia) entidade).getMovimentoInverso().setValor(entidade.getValor());
			((Transferencia) entidade).getMovimentoInverso().setDocumento(entidade.getDocumento());
			((Transferencia) entidade).getMovimentoInverso().setDescricao(entidade.getDescricao());
			((Transferencia) entidade).getMovimentoInverso().setConta(((Transferencia) entidade).getContaInversa());
			((Transferencia) ((Transferencia) entidade).getMovimentoInverso()).setContaInversa(entidade.getConta());
			((Transferencia) entidade).getMovimentoInverso().setReducao(!((Transferencia) entidade).getReducao());
			((Transferencia) ((Transferencia) entidade).getMovimentoInverso()).setMovimentoInverso(entidade);
			listaSalvar.add(((Transferencia) entidade).getMovimentoInverso());
		}
		listaSalvar.add(entidade);
		moviRep.saveAll(listaSalvar);
	}

	@Override
	public void excluir(Movimento entidade) {
		List<Movimento> listaDeletar = new ArrayList<>();
		if (entidade instanceof Transferencia) {
			listaDeletar.add(((Transferencia) entidade).getMovimentoInverso());
		}
		listaDeletar.add(entidade);
		moviRep.deleteAll(listaDeletar);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void validar(Movimento entidade, BindingResult validacao) {
		// VALIDAÇÕES NA INCLUSÃO
		// if (entidade.getIdMovimento() == null) {
		//
		// }
		// // VALIDAÇÕES NA ALTERAÇÃO
		// else {
		//
		// }
		// VALIDAÇÕES EM AMBOS
		// Só permitir lançamento se o período existir e estiver aberto
		if (entidade.getData() != null && entidade instanceof Lancamento) {
			if (!periodoService.haPeriodo(entidade.getData())) {
				validacao.rejectValue("data", "Inexistente");
			} else if (periodoService.ler(entidade.getData()).getEncerrado()) {
				validacao.rejectValue("data", "Final");
			}
		}
		// Não permitir transferência para conta igual
		if (entidade.getConta() != null && entidade instanceof Transferencia
				&& entidade.getConta().equals(((Transferencia) entidade).getContaInversa())) {
			validacao.rejectValue("contaInversa", "Conflito");
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void padronizar(Movimento entidade) {
		if (entidade.getData() == null) {
			entidade.setData(LocalDate.now());
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaLancamentosEntre(Collection<Conta> contas, LocalDate inicio, LocalDate fim, Boolean reducao) {
		if (!contas.isEmpty()) {
			return lr.sumValorByContaInAndDataBetweenAndReducao(contas, inicio, fim, reducao);
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaLancamentosEntre(Collection<Conta> contas, LocalDate inicio, LocalDate fim,
			Subcategoria subcategoria) {
		if (!contas.isEmpty()) {
			return lr.sumValorByContaInAndDataBetweenAndSubcategoria(contas, inicio, fim, subcategoria);
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaLancamentosDesde(Collection<Conta> contas, LocalDate inicio, Boolean reducao) {
		if (!contas.isEmpty()) {
			return lr.sumValorByContaInAndDataGreaterThanEqualAndReducao(contas, inicio, reducao);
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Lancamento> listarLancamentosEntre(Collection<Conta> contas, LocalDate inicio, LocalDate fim) {
		if (!contas.isEmpty()) {
			return lr.findAllByContaInAndDataBetweenOrderByDataAsc(contas, inicio, fim);
		}
		return new ArrayList<>();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaLancamentosPeriodo(Collection<Conta> contas, Periodo periodo, Subcategoria subcategoria) {
		if (!contas.isEmpty() && periodo != null && subcategoria != null) {
			return lr.sumValorByContaInAndPeriodoAndSubcategoria(contas, periodo, subcategoria);
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaLancamentosPeriodo(Collection<Conta> contas, Periodo periodo, Categoria categoria) {
		if (!contas.isEmpty() && periodo != null && categoria != null) {
			return lr.sumValorByContaInAndPeriodoAndSubcategoria_CategoriaPai_OrdemStartingWith(contas,
					periodo, categoria.getOrdem());
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

}
