package com.app.sistconApp.controller;

import com.app.sistconApp.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sindico")
public class PainelController {

    @ModelAttribute("ativo")
    public String[] ativo() {
        return new String[]{"painel", ""};
    }

    @Autowired
    RelatorioService relatorioService;

    @GetMapping({"/", "", "/painel", "/dashboard"})
    public ModelAndView sindico(ModelMap model) {

        model.addAttribute("saldoAtual", relatorioService.saldoAtualTodasContas());
        model.addAttribute("inadimplencia", relatorioService.inadimplenciaAtual());
        model.addAttribute("receitaDespesaMes", relatorioService.receitaDespesaMesAtual());
        model.addAttribute("receitaDespesaRealizada", relatorioService.receitaDespesaRealizadaPeriodoAtual());
        model.addAttribute("receitaDespesaOrcada", relatorioService.receitaDespesaOrcadaPeriodoAtual());

        model.addAttribute("conteudo", "painel");
        return new ModelAndView("fragmentos/layoutSindico", model);
    }

}
