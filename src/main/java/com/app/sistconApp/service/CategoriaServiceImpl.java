package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Categoria;
import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.enums.TipoCategoria;
import com.app.sistconApp.repository.CategoriaRepository;
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
public class CategoriaServiceImpl implements CategoriaService {

	@Autowired
	private CategoriaRepository cr;

	@Autowired
	private UsuarioService usuarioService;

	@Override
	public void salvar(Categoria entidade) {
		if (entidade.getIdCategoria() == null) {
			padronizar(entidade);
			cr.save(entidade);
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public Categoria ler(Long id) {
		return cr.findById(id).get();
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Categoria> listar() {
		Condominio condominio = usuarioService.lerLogado().getCondominio();
		if (condominio == null) {
			return new ArrayList<>();
		}
		return condominio.getCategorias();
	}

	@Override
	public Page<Categoria> listarPagina(Pageable pagina) {
		// TODO Como paginar esta pagina?
		return null;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Categoria> listarReceitas() {
		Condominio condominio = usuarioService.lerLogado().getCondominio();
		if (condominio == null) {
			return new ArrayList<>();
		}
		return cr.findAllByCondominioAndTipo(condominio, TipoCategoria.R);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<Categoria> listarDespesas() {
		Condominio condominio = usuarioService.lerLogado().getCondominio();
		if (condominio == null) {
			return new ArrayList<>();
		}
		return cr.findAllByCondominioAndTipo(condominio, TipoCategoria.D);
	}

	@Override
	public void editar(Categoria entidade) {
		padronizar(entidade);
		cr.save(entidade);
	}

	@Override
	public void excluir(Categoria entidade) {
		cr.delete(entidade);
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void validar(Categoria entidade, BindingResult validacao) {
		// VALIDAÇÕES NA INCLUSÃO
		if (entidade.getIdCategoria() == null) {
			// Ordem não pode repetir
			if (cr.existsByOrdemAndCondominio(entidade.getOrdem(),
					usuarioService.lerLogado().getCondominio())) {
				validacao.rejectValue("ordem", "Unique");
			}
		}
		// VALIDAÇÕES NA ALTERAÇÃO
		else {
			// Ordem não pode repetir
			if (cr.existsByOrdemAndCondominioAndIdCategoriaNot(entidade.getOrdem(),
					usuarioService.lerLogado().getCondominio(), entidade.getIdCategoria())) {
				validacao.rejectValue("ordem", "Unique");
			}
			// Não pode "alterar" o tipo da categoria de RECEITA para DESPESA e vice-versa
			Categoria anterior = ler(entidade.getIdCategoria());
			if (entidade.getTipo() != anterior.getTipo()) {
				validacao.rejectValue("tipo", "Final");
			}
			// Não pode ser filha dela mesma ou de uma das filhas dela
			if ((entidade.getCategoriaPai() != null) && (entidade.getCategoriaPai().equals(entidade)
					|| entidade.getCategoriaPai().getOrdem().startsWith(ler(entidade.getIdCategoria()).getOrdem()))) {
				validacao.rejectValue("categoriaPai", "typeMismatch", new Object[] { 0, "é igual ou inferior a esta" },
						null);
			}
		}
		// VALIDAÇÕES EM AMBOS
		if (entidade.getCategoriaPai() != null) {
			// Não pode criar mais níveis do que o parametrizado
			if (entidade.getCategoriaPai().getNivel() >= Categoria.NIVEL_MAX) {
				validacao.rejectValue("categoriaPai", "Max", new Object[] { 0, Categoria.NIVEL_MAX }, null);
			}
			// Não pode ter um tipo diferente do tipo do pai
			if (entidade.getCategoriaPai().getTipo() != entidade.getTipo()) {
				validacao.rejectValue("tipo", "typeMismatch");
			}
			// Ordem tem que ser sequência da categoria superior
			if (!entidade.getOrdem().startsWith(entidade.getCategoriaPai().getOrdem())) {
				validacao.rejectValue("ordem", "typeMismatch");
			}
		}
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public void padronizar(Categoria entidade) {
		if (entidade.getCondominio() == null) {
			entidade.setCondominio(usuarioService.lerLogado().getCondominio());
		}
		Categoria categoriaPai = entidade.getCategoriaPai();
		if (categoriaPai != null) {
			entidade.setNivel(categoriaPai.getNivel() + 1);
		} else {
			entidade.setNivel(1);
		}

	}

}
