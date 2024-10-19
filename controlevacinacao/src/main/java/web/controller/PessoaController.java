package web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web.model.Pessoa;
import web.model.Vacina;
import web.repository.PessoaRepository;
import web.service.PessoaService;

import java.util.List;

@Controller
@RequestMapping("/pessoas")
public class PessoaController {

    private Logger logger = LoggerFactory.getLogger(PessoaController.class);
    private final PessoaRepository repository;
    private final PessoaService vacinaService;

    public PessoaController(PessoaRepository repository, PessoaService vacinaService) {
        this.repository = repository;
        this.vacinaService = vacinaService;
    }

    @GetMapping("/todas")
    public String todasPessoas(Model model) {
        List<Pessoa> pessoas = repository.findAll();
        model.addAttribute("pessoas", pessoas);

//        logger.info("Exibindo todas as vacinas: {}", vacinas);

        return "pessoas/todas";
    }



}
