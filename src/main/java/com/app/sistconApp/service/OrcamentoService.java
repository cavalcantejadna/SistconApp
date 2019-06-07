package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Categoria;
import com.app.sistconApp.modelo.Orcamento;
import com.app.sistconApp.modelo.Periodo;
import com.app.sistconApp.modelo.Subcategoria;
import com.app.sistconApp.modelo.enums.TipoCategoria;
import java.math.BigDecimal;

public interface OrcamentoService extends CrudService<Orcamento, Long> {

    public BigDecimal somaOrcamentos(Periodo periodo, TipoCategoria tipo);

    public BigDecimal somaOrcamentos(Periodo periodo, Categoria categoria);

    public Orcamento ler(Periodo periodo, Subcategoria subcategoria);

}
