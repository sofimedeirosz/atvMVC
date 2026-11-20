package com.example.jesus.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.jesus.model.Categoria;
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
@RequestMapping("/categorias")
public class CategoriaController {

    private List<Categoria> categorias = new ArrayList<>();

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categorias);
        return "categorias/listar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@RequestBody Categoria categoria) {
        categorias.add(categoria);
        return "redirect:/categorias";
    }

    @PutMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @RequestBody Categoria categoria) {
        if (indice >= 0 && indice < categorias.size()) {
            categorias.set(indice, categoria);
        }
        return "redirect:/categorias";
    }

    @DeleteMapping("/excluir/{indice}")
    public String excluir(@PathVariable int indice) {
        if (indice >= 0 && indice < categorias.size()) {
            categorias.remove(indice);
        }
        return "redirect:/categorias";
    }
}
