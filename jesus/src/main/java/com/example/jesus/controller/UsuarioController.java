package com.example.jesus.controller;

import java.util.ArrayList;
import java.util.List;

import com.example.jesus.model.Usuario;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    private List<Usuario> usuarios = new ArrayList<>();

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarios);
        return "usuarios/listar";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@RequestBody Usuario usuario) {
        usuarios.add(usuario);
        return "redirect:/usuarios";
    }

    @PutMapping("/editar/{indice}")
    public String editar(@PathVariable int indice,
                         @RequestBody Usuario usuario) {
        if (indice >= 0 && indice < usuarios.size()) {
            usuarios.set(indice, usuario);
        }
        return "redirect:/usuarios";
    }

    @DeleteMapping("/excluir/{indice}")
    public String excluir(@PathVariable int indice) {
        if (indice >= 0 && indice < usuarios.size()) {
            usuarios.remove(indice);
        }
        return "redirect:/usuarios";
    }
}
