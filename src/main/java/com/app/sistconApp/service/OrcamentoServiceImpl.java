package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Categoria;
import com.app.sistconApp.modelo.Orcamento;
import com.app.sistconApp.modelo.Periodo;
import com.app.sistconApp.modelo.Subcategoria;
import com.app.sistconApp.modelo.enums.TipoCategoria;
import com.app.sistconApp.repository.OrcamentoRepository;
import java.math.BigDecimal;
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
public class OrcamentoServiceImpl implements OrcamentoService {

	@Autowired
	private OrcamentoRepository orcRep;

	@Autowired
	private PeriodoService periodoService;

	@Override
	public void salvar(Orcamento entidade) {
		if (entidade.getIdOrcamento() == null) {
			orcRep.save(entidade);
		}

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Orcamento ler(Long id) {
		return orcRep.findById(id).get();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Orcamento> listar() {
		return orcRep.findAllByPeriodoInOrderByPeriodoDescSubcategoriaAsc(periodoService.listar());
	}

	@Override
	public Page<Orcamento> listarPagina(Pageable pagina) {
		return orcRep.findAllByPeriodoInOrderByPeriodoDescSubcategoriaAsc(periodoService.listar(), pagina);
	}

	@Override
	public void editar(Orcamento entidade) {
		orcRep.save(entidade);

	}

	@Override
	public void excluir(Orcamento entidade) {
		orcRep.delete(entidade);

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void validar(Orcamento entidade, BindingResult validacao) {
		// VALIDAÇÕES NA INCLUSÃO
		if (entidade.getIdOrcamento() == null) {
			// Não permitir incluir orçamento repetido
			if (entidade.getPeriodo() != null && entidade.getSubcategoria() != null
					&& orcRep.existsByPeriodoAndSubcategoria(entidade.getPeriodo(), entidade.getSubcategoria())) {
				validacao.rejectValue("subcategoria", "Unique");
			}
		}
		// VALIDAÇÕES NA ALTERAÇÃO
		else {
			// Não permitir um orçamento repetido
			if (entidade.getPeriodo() != null && entidade.getSubcategoria() != null
					&& orcRep.existsByPeriodoAndSubcategoriaAndIdOrcamentoNot(entidade.getPeriodo(),
							entidade.getSubcategoria(), entidade.getIdOrcamento())) {
				validacao.rejectValue("subcategoria", "Unique");
			}
		}
		// VALIDAÇÕES EM AMBOS
		if (entidade.getPeriodo() != null && entidade.getPeriodo().getEncerrado()) {
			validacao.rejectValue("periodo", "Final");
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void padronizar(Orcamento entidade) {
		// Nada a padronizar por enquanto

	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaOrcamentos(Periodo periodo, TipoCategoria tipo) {
		if (periodo != null) {
			return orcRep.sumByPeriodoAndSubcategoria_CategoriaPai_Tipo(periodo, tipo);
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public BigDecimal somaOrcamentos(Periodo periodo, Categoria categoria) {
		if (periodo != null && categoria != null) {
			return orcRep.sumByPeriodoAndSubcategoria_CategoriaPai_OrdemStartingWith(periodo,
					categoria.getOrdem());
		} else {
			return BigDecimal.ZERO.setScale(2);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Orcamento ler(Periodo periodo, Subcategoria subcategoria) {
		return orcRep.findOneByPeriodoAndSubcategoria(periodo, subcategoria);
	}

}
