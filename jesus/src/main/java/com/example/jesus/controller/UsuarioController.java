package com.example.jesus.controller;

import com.example.jesus.model.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsuarioController{
    @GetMapping("/usuario")
    public String exibirUsuario(Model model){

        Usuario usuario = new Usuario();
        usuario.setUsuario("sofia123");
        usuario.setCpf("11122233344");
        usuario.setNome("Sofia");
        usuario.setSenha("sofia4321");

        model.addAttribute("usuario", usuario);

        return "usuario";
    }
}
