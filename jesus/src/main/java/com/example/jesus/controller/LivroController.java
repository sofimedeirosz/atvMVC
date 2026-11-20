package com.example.jesus.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.jesus.model.Livro;
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
@RequestMapping("/livros")
public class LivroController {

    private List<Livro> livros = new ArrayList<>();

    @GetMapping("/inicio")
    public String inicio() {
        return "livros/inicio";
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livros);
        return "livros/listar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@RequestBody Livro livro) {
        livros.add(livro);
        return "redirect:/livros";
    }

    @PutMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @RequestBody Livro livro) {
        if (indice >= 0 && indice < livros.size()) {
            livros.set(indice, livro);
        }
        return "redirect:/livros";
    }

    @DeleteMapping("/excluir/{indice}")
    public String excluir(@PathVariable int indice) {
        if (indice >= 0 && indice < livros.size()) {
            livros.remove(indice);
        }
        return "redirect:/livros";
    }
}

