package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.Periodo;
import com.app.sistconApp.repository.PeriodoRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class PeriodoServiceImpl implements PeriodoService {

	@Autowired
	private PeriodoRepository pr;

	@Autowired
	private UsuarioService usuarioService;

	@Override
	public void salvar(Periodo entidade) {
		if (entidade.getIdPeriodo() == null) {
			padronizar(entidade);
			pr.save(entidade);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Periodo ler(Long id) {
		return pr.findById(id).get();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Periodo> listar() {
		Condominio condominio = usuarioService.lerLogado().getCondominio();
		if (condominio == null) {
			return new ArrayList<>();
		}
		return condominio.getPeriodos();
	}

	@Override
	public Page<Periodo> listarPagina(Pageable pagina) {
		Condominio condominio = usuarioService.lerLogado().getCondominio();
		if (condominio == null) {
			return Page.empty(pagina);
		}
		return pr.findAllByCondominioOrderByInicioDesc(condominio, pagina);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public boolean haPeriodo(LocalDate data) {
		return pr.existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqual(
				usuarioService.lerLogado().getCondominio(), data, data);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Periodo ler(LocalDate data) {
		return pr.findOneByCondominioAndInicioLessThanEqualAndFimGreaterThanEqual(
				usuarioService.lerLogado().getCondominio(), data, data);
	}

	@Override
	public void editar(Periodo entidade) {
		padronizar(entidade);
		pr.save(entidade);
	}

	@Override
	public void excluir(Periodo entidade) {
		pr.delete(entidade);

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void validar(Periodo entidade, BindingResult validacao) {
		// VALIDAÇÕES NA INCLUSÃO
		if (entidade.getIdPeriodo() == null) {
			if (entidade.getInicio() != null && entidade.getFim() != null) {
				// Não pode repetir período
				if (pr.existsByCondominioAndInicioAfterAndFimBefore(usuarioService.lerLogado().getCondominio(),
						entidade.getInicio(), entidade.getFim())) {
					validacao.rejectValue("inicio", "Conflito");
					validacao.rejectValue("fim", "Conflito");
				} else {
					if (pr.existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqual(
							usuarioService.lerLogado().getCondominio(), entidade.getInicio(), entidade.getInicio())) {
						validacao.rejectValue("inicio", "Unique");
					}
					if (pr.existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqual(
							usuarioService.lerLogado().getCondominio(), entidade.getFim(), entidade.getFim())) {
						validacao.rejectValue("fim", "Unique");
					}
				}
			}
		}
		// VALIDAÇÕES NA ALTERAÇÃO
		else {
			if (entidade.getInicio() != null && entidade.getFim() != null) {
				// Não pode repetir período
				if (pr.existsByCondominioAndInicioAfterAndFimBeforeAndIdPeriodoNot(
						usuarioService.lerLogado().getCondominio(), entidade.getInicio(), entidade.getFim(),
						entidade.getIdPeriodo())) {
					validacao.rejectValue("inicio", "Conflito");
					validacao.rejectValue("fim", "Conflito");
				} else {
					if (pr.existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqualAndIdPeriodoNot(
							usuarioService.lerLogado().getCondominio(), entidade.getInicio(), entidade.getInicio(),
							entidade.getIdPeriodo())) {
						validacao.rejectValue("inicio", "Unique");
					}
					if (pr.existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqualAndIdPeriodoNot(
							usuarioService.lerLogado().getCondominio(), entidade.getFim(), entidade.getFim(),
							entidade.getIdPeriodo())) {
						validacao.rejectValue("fim", "Unique");
					}
				}
			}
		}
		// VALIDAÇÕES EM AMBOS
		// Data final não pode ser menor que a inicial
		if (entidade.getInicio() != null && entidade.getFim() != null
				&& entidade.getFim().isBefore(entidade.getInicio())) {
			validacao.rejectValue("fim", "typeMismatch");
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void padronizar(Periodo entidade) {
		if (entidade.getCondominio() == null) {
			entidade.setCondominio(usuarioService.lerLogado().getCondominio());
		}
		if (entidade.getEncerrado() == null) {
			entidade.setEncerrado(Boolean.FALSE);
		}
	}

}
