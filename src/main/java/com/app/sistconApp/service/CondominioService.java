package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Condominio;




public interface CondominioService extends CrudService<Condominio, Long> {

	public Condominio ler();

}
