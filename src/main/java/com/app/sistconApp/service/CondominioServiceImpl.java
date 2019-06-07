/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Condominio;
import com.app.sistconApp.modelo.Usuario;
import com.app.sistconApp.repository.CondominioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.validation.BindingResult;

/**
 *
 * @author User
 */
@Service
@Transactional
public class CondominioServiceImpl implements CondominioService {

    // FIXME ao ler/editar/excluir, tratar casos do usuário alterar o ID na URL ou
    // fonte e enxergar entidades de outro usuário (todas as classes)
    @Autowired
    private CondominioRepository cr;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void salvar(Condominio condominio) {
        if (condominio.getIdCondominio() == null) {
            padronizar(condominio);
            cr.save(condominio);

            // Atualizar o ID do condomínio no cadastro do síndico
            Usuario sindico = usuarioService.lerLogado();
            sindico.setCondominio(condominio);
            usuarioService.editar(sindico);
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Condominio ler() {
        return usuarioService.lerLogado().getCondominio();
    }

    @Override
    public void editar(Condominio condominio) {
        padronizar(condominio);
        cr.save(condominio);
    }

    @Override
    public void excluir(Condominio condominio) {
        cr.delete(condominio);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Condominio ler(Long id) {
        // LATER implementar ao fazer o usuário tipo ADMIN
        return null;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Condominio> listar() {
        // LATER implementar ao fazer o usuário tipo ADMIN
        return null;
    }

    @Override
    public Page<Condominio> listarPagina(Pageable pagina) {
        // LATER criar este método quando fizer página de listar condomínios (admin)
        return null;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void validar(Condominio entidade, BindingResult validacao) {
        // VALIDAÇÕES NA INCLUSÃO
        if (entidade.getIdCondominio() == null) {
            // CNPJ não pode repetir
            if (entidade.getCnpj() != null && cr.existsByCnpj(entidade.getCnpj())) {
                validacao.rejectValue("cnpj", "Unique");
            }
        } // VALIDAÇÕES NA ALTERAÇÃO
        else {
            // CNPJ não pode repetir
            if (entidade.getCnpj() != null
                    && cr.existsByCnpjAndIdCondominioNot(entidade.getCnpj(), entidade.getIdCondominio())) {
                validacao.rejectValue("cnpj", "Unique");
            }
        }
        // VALIDAÇÕES EM AMBOS
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public void padronizar(Condominio entidade) {
        // Nada a padronizar por enquanto
    }

}
