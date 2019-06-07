/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.PessoaJuridica;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface PessoaJuridicaRepository extends PagingAndSortingRepository<PessoaJuridica, Long> {

    Boolean existsByCnpjAndCondominio(String cnpj, Condominio condominio);

    Boolean existsByCnpjAndCondominioAndIdPessoaNot(String cnpj, Condominio condominio, Long idPessoa);
}
