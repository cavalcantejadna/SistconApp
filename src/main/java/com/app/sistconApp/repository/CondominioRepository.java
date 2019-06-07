/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.app.sistconApp.modelo.Condominio;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface CondominioRepository extends PagingAndSortingRepository<Condominio, Long> {
           
        Boolean existsByCnpj(String cnpj);

	Boolean existsByCnpjAndIdCondominioNot(String cnpj, Long idCondominio);
}
