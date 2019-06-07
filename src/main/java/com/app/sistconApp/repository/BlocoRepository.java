/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Bloco;
import com.app.sistconApp.modelo.Condominio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface BlocoRepository extends PagingAndSortingRepository<Bloco, Long>{
    
        Boolean existsBySiglaAndCondominio(String sigla, Condominio condominio);

	Boolean existsBySiglaAndCondominioAndIdBlocoNot(String sigla, Condominio condominio, Long idBloco);

	Page<Bloco> findAllByCondominioOrderBySiglaAsc(Condominio condominio, Pageable pagina);
}
