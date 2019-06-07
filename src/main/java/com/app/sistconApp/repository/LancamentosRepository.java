/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Conta;
import com.app.sistconApp.modelo.Lancamento;
import com.app.sistconApp.modelo.Periodo;
import com.app.sistconApp.modelo.Subcategoria;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface LancamentosRepository extends PagingAndSortingRepository<Lancamento, Long> {

    @Query("select sum(valor) from #{#entityName} l where l.conta in :contas and l.data between :dataInicial and :dataFinal and l.reducao = :reducao")
    BigDecimal sumValorByContaInAndDataBetweenAndReducao(@Param("contas") Collection<Conta> contas,
            @Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal,
            @Param("reducao") Boolean reducao);

    @Query("select sum(valor) from #{#entityName} l where l.conta in :contas and l.data >= :data and l.reducao = :reducao")
    BigDecimal sumValorByContaInAndDataGreaterThanEqualAndReducao(@Param("contas") Collection<Conta> contas,
            @Param("data") LocalDate data, @Param("reducao") Boolean reducao);

    @Query("select sum(valor) from #{#entityName} l where l.conta in :contas and l.data between :dataInicial and :dataFinal and l.subcategoria = :subcategoria ")
    BigDecimal sumValorByContaInAndDataBetweenAndSubcategoria(@Param("contas") Collection<Conta> contas,
            @Param("dataInicial") LocalDate dataInicial, @Param("dataFinal") LocalDate dataFinal,
            @Param("subcategoria") Subcategoria subcategoria);

    @Query("select sum(valor) from #{#entityName} l where l.conta in :contas and l.periodo = :periodo and l.subcategoria = :subcategoria ")
    BigDecimal sumValorByContaInAndPeriodoAndSubcategoria(@Param("contas") Collection<Conta> contas,
            @Param("periodo") Periodo periodo, @Param("subcategoria") Subcategoria subcategoria);

    @Query("select sum(valor) from #{#entityName} l where l.conta in :contas and l.periodo = :periodo and l.subcategoria.categoriaPai.ordem like :ordem%")
    BigDecimal sumValorByContaInAndPeriodoAndSubcategoria_CategoriaPai_OrdemStartingWith(
            @Param("contas") Collection<Conta> contas, @Param("periodo") Periodo periodo, @Param("ordem") String ordem);

    List<Lancamento> findAllByContaInAndDataBetweenOrderByDataAsc(Collection<Conta> conta, LocalDate inicio,
            LocalDate fim);
}
