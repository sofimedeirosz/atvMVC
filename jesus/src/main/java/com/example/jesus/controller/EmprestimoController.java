package com.example.jesus.controller;

import java.util.ArrayList;
import java.util.List;
import com.example.jesus.model.Emprestimo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private List<Emprestimo> emprestimos = new ArrayList<>();

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("emprestimos", emprestimos);
        return "emprestimos/listar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute Emprestimo emprestimo) {
        emprestimos.add(emprestimo);
        return "redirect:/emprestimos";
    }

    @GetMapping("/editar/{indice}")
    public String formularioEditar(@PathVariable int indice, Model model) {
        if (indice >= 0 && indice < emprestimos.size()) {
            model.addAttribute("emprestimo", emprestimos.get(indice));
            model.addAttribute("indice", indice);
            return "emprestimos/editar";
        }
        return "redirect:/emprestimos";
    }

    @PostMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @ModelAttribute Emprestimo emprestimo) {
        if (indice >= 0 && indice < emprestimos.size()) {
            emprestimos.set(indice, emprestimo);
        }
        return "redirect:/emprestimos";
    }

    @DeleteMapping("/excluir/{indice}")
    public String excluir(@PathVariable int indice) {
        if (indice >= 0 && indice < emprestimos.size()) {
            emprestimos.remove(indice);
        }
        return "redirect:/emprestimos";
    }
}
