package com.app.sistconApp.service;

import com.app.sistconApp.modelo.Periodo;
import java.time.LocalDate;

public interface PeriodoService extends CrudService<Periodo, Long> {

    public boolean haPeriodo(LocalDate data);

    public Periodo ler(LocalDate data);

}
