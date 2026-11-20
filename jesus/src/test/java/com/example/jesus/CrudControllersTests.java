package com.example.jesus;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CrudControllersTests {

    @Autowired
    private MockMvc mvc;

    record Caso(String base, String atributo, MultiValueMap<String, String> cadastro,
                MultiValueMap<String, String> edicao, String original, String alterado) {}

    static Stream<Caso> casos() {
        return Stream.of(
                new Caso("livros", "livro",
                        campos("titulo", "Dom Casmurro", "autor", "Machado de Assis", "isbn", "9788535914849"),
                        campos("titulo", "Memorias Postumas", "autor", "Machado de Assis", "isbn", "9788535914856"),
                        "Dom Casmurro", "Memorias Postumas"),
                new Caso("autores", "autor",
                        campos("nome", "Machado de Assis", "nacionalidade", "Brasileira", "obrasPublicadas", "10"),
                        campos("nome", "Raphael Montes", "nacionalidade", "Brasileira", "obrasPublicadas", "12"),
                        "Machado de Assis", "Raphael Montes"),
                new Caso("categorias", "categoria",
                        campos("genero", "Terror"), campos("genero", "Romance"),
                        "Terror", "Romance"),
                new Caso("emprestimos", "emprestimo",
                        campos("statusEmprestimo", "Emprestado", "dataEmprestimo", "20/11/2026"),
                        campos("statusEmprestimo", "Devolvido", "dataEmprestimo", "21/11/2026"),
                        "Emprestado", "Devolvido"),
                new Caso("usuarios", "usuario",
                        campos("nome", "Sofia", "cpf", "11122233344", "usuario", "sofia123", "senha", "SegredoInicial123"),
                        campos("nome", "Sofia Silva", "cpf", "55566677788", "usuario", "sofia.silva", "senha", "NovoSegredo456"),
                        "sofia123", "sofia.silva"));
    }

    private static MultiValueMap<String, String> campos(String... pares) {
        MultiValueMap<String, String> campos = new LinkedMultiValueMap<>();
        for (int i = 0; i < pares.length; i += 2) {
            campos.add(pares[i], pares[i + 1]);
        }
        return campos;
    }

    @Test
    void inicioTemLinksParaAsCincoListagens() throws Exception {
        String html = mvc.perform(get("/livros/inicio"))
                .andExpect(status().isOk())
                .andExpect(view().name("livros/inicio"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        for (Caso caso : casos().toList()) {
            assertThat(html).contains("href=\"/" + caso.base() + "\"");
        }
    }

    @ParameterizedTest
    @MethodSource("casos")
    void crudComFormulariosEIndices(Caso caso) throws Exception {
        String base = "/" + caso.base();
        MvcResult resultado = listar(caso);
        assertThat(registros(resultado)).isEmpty();
        assertThat(html(resultado)).contains("action=\"" + base + "/cadastrar\"", "method=\"post\"",
                "href=\"/livros/inicio\"");
        for (String campo : caso.cadastro().keySet()) {
            assertThat(html(resultado)).contains("name=\"" + campo + "\"");
        }

        // Dois cadastros permitem conferir o deslocamento dos índices na exclusão.
        for (int i = 0; i < 2; i++) {
            mvc.perform(post(base + "/cadastrar")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(caso.cadastro()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
        }
        resultado = listar(caso);
        assertThat(registros(resultado)).hasSize(2);
        assertThat(html(resultado)).contains(caso.original(), "<td>0</td>", "<td>1</td>",
                "href=\"" + base + "/editar/0\"", "href=\"" + base + "/editar/1\"",
                "action=\"" + base + "/excluir/0\"", "action=\"" + base + "/excluir/1\"",
                "<input type=\"hidden\" name=\"_method\" value=\"DELETE\">");
        verificarEdicaoPreenchida(caso, caso.cadastro());

        for (int indice : new int[] {-1, 2}) {
            mvc.perform(get(base + "/editar/" + indice))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
            mvc.perform(post(base + "/editar/" + indice)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .params(caso.edicao()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
            mvc.perform(post(base + "/excluir/" + indice)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .param("_method", "DELETE"))
                    .andExpect(handler().methodName("excluir"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
        }
        assertThat(registros(listar(caso))).hasSize(2);
        assertThat(html(listar(caso))).contains(caso.original()).doesNotContain(caso.alterado());

        // A exclusão precisa passar pelo DELETE, inclusive quando vem de um formulário.
        mvc.perform(post(base + "/excluir/0")).andExpect(status().isMethodNotAllowed());
        mvc.perform(get(base + "/excluir/0")).andExpect(status().isMethodNotAllowed());
        assertThat(registros(listar(caso))).hasSize(2);
        mvc.perform(post(base + "/excluir/0")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("_method", "DELETE"))
                .andExpect(handler().methodName("excluir"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(base));
        resultado = listar(caso);
        assertThat(registros(resultado)).hasSize(1);
        assertThat(html(resultado)).contains("<td>0</td>").doesNotContain("<td>1</td>");
        verificarEdicaoPreenchida(caso, caso.cadastro());

        mvc.perform(post(base + "/editar/0")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(caso.edicao()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(base));
        resultado = listar(caso);
        assertThat(registros(resultado)).hasSize(1);
        assertThat(html(resultado)).contains(caso.alterado()).doesNotContain(caso.original());
        assertThat(html(resultado)).doesNotContain("SegredoInicial123", "NovoSegredo456", "<script");
        verificarEdicaoPreenchida(caso, caso.edicao());

        // O DELETE direto continua disponível no mesmo endpoint.
        mvc.perform(delete(base + "/excluir/0"))
                .andExpect(handler().methodName("excluir"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(base));
        assertThat(registros(listar(caso))).isEmpty();
        mvc.perform(get(base + "/editar/0"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(base));
    }

    private void verificarEdicaoPreenchida(Caso caso, MultiValueMap<String, String> valores) throws Exception {
        String base = "/" + caso.base();
        MvcResult resultado = mvc.perform(get(base + "/editar/0"))
                .andExpect(status().isOk())
                .andExpect(view().name(caso.base() + "/editar"))
                .andExpect(model().attribute("indice", 0))
                .andExpect(model().attributeExists(caso.atributo()))
                .andReturn();
        String html = html(resultado);
        assertThat(html).contains("action=\"" + base + "/editar/0\"", "method=\"post\"",
                ">Salvar</button>", "href=\"" + base + "\">Voltar</a>");
        for (String campo : valores.keySet()) {
            assertThat(html).containsPattern("<input\\b(?=[^>]*\\bname=\"" + campo
                    + "\")(?=[^>]*\\bvalue=\"" + Pattern.quote(valores.getFirst(campo)) + "\")[^>]*>");
        }
        if (caso.base().equals("usuarios")) {
            assertThat(html).contains("type=\"password\"");
        }
    }

    private MvcResult listar(Caso caso) throws Exception {
        return mvc.perform(get("/" + caso.base()))
                .andExpect(status().isOk())
                .andExpect(view().name(caso.base() + "/listar"))
                .andExpect(model().attributeExists(caso.base()))
                .andReturn();
    }

    private List<?> registros(MvcResult resultado) {
        return (List<?>) resultado.getModelAndView().getModel()
                .get(resultado.getModelAndView().getViewName().split("/")[0]);
    }

    private String html(MvcResult resultado) throws Exception {
        return resultado.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }
}
