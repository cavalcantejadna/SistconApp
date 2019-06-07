/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.Periodo;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Fernandes
 */
@Repository
public interface PeriodoRepository extends PagingAndSortingRepository<Periodo, Long> {

    Page<Periodo> findAllByCondominioOrderByInicioDesc(Condominio condominio, Pageable pagina);

    boolean existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqual(Condominio condominio, LocalDate inicio,
            LocalDate fim);

    boolean existsByCondominioAndInicioLessThanEqualAndFimGreaterThanEqualAndIdPeriodoNot(Condominio condominio,
            LocalDate inicio, LocalDate fim, Long idPeriodo);

    boolean existsByCondominioAndInicioAfterAndFimBefore(Condominio condominio, LocalDate inicio, LocalDate fim);

    boolean existsByCondominioAndInicioAfterAndFimBeforeAndIdPeriodoNot(Condominio condominio, LocalDate inicio,
            LocalDate fim, Long idPeriodo);

    Periodo findOneByCondominioAndInicioLessThanEqualAndFimGreaterThanEqual(Condominio condominio, LocalDate inicio,
            LocalDate fim);
}
