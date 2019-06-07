/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Categoria;
import com.app.sistconApp.modelo.Subcategoria;
import java.util.Collection;
import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface SubcategoriaRepository extends PagingAndSortingRepository<Subcategoria, Long> {

    List<Subcategoria> findAllByCategoriaPaiInOrderByCategoriaPai_OrdemAscDescricao(Collection<Categoria> categoriaPai);

    int countByCategoriaPaiIn(Collection<Categoria> categoriaPai);

    Boolean existsByDescricaoAndCategoriaPai(String descricao, Categoria categoriaPai);

    Boolean existsByDescricaoAndCategoriaPaiAndIdSubcategoriaNot(String descricao, Categoria categoriaPai,
            Long idSubcategoria);
}
