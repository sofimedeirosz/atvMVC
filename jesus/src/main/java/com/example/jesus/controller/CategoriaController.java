package com.example.jesus.controller;

import com.example.jesus.model.Categoria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoriaController{
    @GetMapping("/categoria")
    public String exibirCategoria(Model model){

        Categoria categoria = new Categoria();
        categoria.setGenero("Terror");

        model.addAttribute("categoria", categoria);

        return "categoria";
    }
}
