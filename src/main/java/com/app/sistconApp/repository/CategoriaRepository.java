/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Categoria;
import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.enums.TipoCategoria;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface CategoriaRepository extends PagingAndSortingRepository<Categoria, Long> {

    Boolean existsByOrdemAndCondominio(String ordem, Condominio condominio);

    Boolean existsByOrdemAndCondominioAndIdCategoriaNot(String ordem, Condominio condominio, Long idCategoria);

    List<Categoria> findAllByCondominioAndTipo(Condominio condominio, TipoCategoria tipo);

}
