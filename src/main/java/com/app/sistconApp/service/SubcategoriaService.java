package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Subcategoria;
import java.util.List;

public interface SubcategoriaService extends CrudService<Subcategoria, Long> {

    public int contagem();

    public List<Subcategoria> listarReceitas();

    public List<Subcategoria> listarDespesas();

}
