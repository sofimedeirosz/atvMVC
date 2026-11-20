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
import org.springframework.web.bind.annotation.ModelAttribute;
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
    public String cadastrar(@ModelAttribute Categoria categoria) {
        categorias.add(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{indice}")
    public String formularioEditar(@PathVariable int indice, Model model) {
        if (indice >= 0 && indice < categorias.size()) {
            model.addAttribute("categoria", categorias.get(indice));
            model.addAttribute("indice", indice);
            return "categorias/editar";
        }
        return "redirect:/categorias";
    }

    @PostMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @ModelAttribute Categoria categoria) {
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
