package com.example.jesus.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.jesus.model.Autor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/autores")
public class AutorController {

    private List<Autor> autores = new ArrayList<>();

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("autores", autores);
        return "autores/listar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@RequestBody Autor autor) {
        autores.add(autor);
        return "redirect:/autores";
    }

    @PutMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @RequestBody Autor autor) {
        if (indice >= 0 && indice < autores.size()) {
            autores.set(indice, autor);
        }
        return "redirect:/autores";
    }

    @DeleteMapping("/excluir/{indice}")
    public String excluir(@PathVariable int indice) {
        if (indice >= 0 && indice < autores.size()) {
            autores.remove(indice);
        }
        return "redirect:/autores";
    }
}
