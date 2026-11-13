package com.example.jesus.controller;

import com.example.jesus.model.Autor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AutorController{
    @GetMapping("/autor")
    public String exibirAutor(Model model){

        Autor autor = new Autor();
        autor.setNacionalidade("Brasileiro");
        autor.setNome("Raphael Montes");
        autor.setObrasPublicadas(10);

        model.addAttribute("autor", autor);

        return "autor";
    }
}
