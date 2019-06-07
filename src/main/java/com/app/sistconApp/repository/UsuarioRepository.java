/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.repository;

import com.app.sistconApp.modelo.Usuario;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marcelo Feranades
 */
@Repository
public interface UsuarioRepository extends PagingAndSortingRepository<Usuario, Long>{
    
        Usuario findOneByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByUsernameAndIdNot(String username, Long id);
}
