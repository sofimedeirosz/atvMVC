package com.example.jesus.controller;

import com.example.jesus.model.Emprestimo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmprestimoController{
    @GetMapping("/emprestimo")
    public String exibirEmprestimo(Model model){

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataEmprestimo("12/11/2026");
        emprestimo.setStatusEmprestimo("disponivel");

        model.addAttribute("emprestimo", emprestimo);

        return "emprestimo";
    }
}
