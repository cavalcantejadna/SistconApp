/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Conta;
import com.app.sistconApp.modelo.Movimento;
import java.time.LocalDate;
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
public interface MovimentoRepository extends PagingAndSortingRepository<Movimento, Long> {

    List<Movimento> findAllByContaInOrderByDataDesc(Collection<Conta> conta);

    Page<Movimento> findAllByContaInOrderByDataDesc(Collection<Conta> conta, Pageable pageable);

    List<Movimento> findAllByContaInAndDataBetweenOrderByDataAsc(Collection<Conta> conta, LocalDate inicio,
            LocalDate fim);
}
