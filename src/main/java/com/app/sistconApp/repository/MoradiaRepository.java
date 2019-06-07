/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Bloco;
import com.app.sistconApp.modelo.Moradia;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface MoradiaRepository extends PagingAndSortingRepository<Moradia, Long> {

    List<Moradia> findAllByBlocoInOrderByBlocoAscSiglaAsc(Collection<Bloco> bloco);

    Page<Moradia> findAllByBlocoInOrderByBlocoAscSiglaAsc(Collection<Bloco> bloco, Pageable pagina);

    Boolean existsBySiglaAndBloco(String sigla, Bloco bloco);

    Boolean existsBySiglaAndBlocoAndIdMoradiaNot(String sigla, Bloco bloco, Long idMoradia);
}
