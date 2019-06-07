package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Categoria;
import java.util.List;


public interface CategoriaService extends CrudService<Categoria, Long> {

	public List<Categoria> listarReceitas();

	public List<Categoria> listarDespesas();

}
