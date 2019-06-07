/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.Conta;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface ContaRepository extends PagingAndSortingRepository<Conta, Long> {

    Page<Conta> findAllByCondominioOrderBySiglaAsc(Condominio condominio, Pageable pagina);

    Boolean existsBySiglaAndCondominio(String sigla, Condominio condominio);

    Boolean existsBySiglaAndCondominioAndIdContaNot(String sigla, Condominio condominio, Long idConta);

    @Query("select sum(saldoAtual) from #{#entityName} c where c.condominio = :condominio")
    BigDecimal sumSaldoAtualByCondominio(@Param("condominio") Condominio condominio);
}
