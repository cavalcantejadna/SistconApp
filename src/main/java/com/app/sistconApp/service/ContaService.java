package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Conta;
import java.math.BigDecimal;

public interface ContaService extends CrudService<Conta, Long> {

    public BigDecimal saldoAtual();

}
