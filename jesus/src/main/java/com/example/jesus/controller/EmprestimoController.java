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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public String cadastrar(@RequestBody Emprestimo emprestimo) {
        emprestimos.add(emprestimo);
        return "redirect:/emprestimos";
    }

    @PutMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @RequestBody Emprestimo emprestimo) {
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
