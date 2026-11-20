package com.example.jesus;

import java.nio.charset.StandardCharsets;
import java.util.List;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CrudControllersTests {

    @Autowired
    private MockMvc mvc;

    record Caso(String base, String cadastro, String edicao, String original, String alterado) {}

    static Stream<Caso> casos() {
        return Stream.of(
                new Caso("livros", "{\"titulo\":\"Dom Casmurro\",\"autor\":\"Machado de Assis\",\"isbn\":\"9788535914849\"}", "{\"titulo\":\"Memorias Postumas\",\"autor\":\"Machado de Assis\",\"isbn\":\"9788535914856\"}", "Dom Casmurro", "Memorias Postumas"),
                new Caso("autores", "{\"nome\":\"Machado de Assis\",\"nacionalidade\":\"Brasileira\",\"obrasPublicadas\":10}", "{\"nome\":\"Raphael Montes\",\"nacionalidade\":\"Brasileira\",\"obrasPublicadas\":12}", "Machado de Assis", "Raphael Montes"),
                new Caso("categorias", "{\"genero\":\"Terror\"}", "{\"genero\":\"Romance\"}", "Terror", "Romance"),
                new Caso("emprestimos", "{\"statusEmprestimo\":\"Emprestado\",\"dataEmprestimo\":\"20/11/2026\"}", "{\"statusEmprestimo\":\"Devolvido\",\"dataEmprestimo\":\"21/11/2026\"}", "Emprestado", "Devolvido"),
                new Caso("usuarios", "{\"nome\":\"Sofia\",\"cpf\":\"11122233344\",\"usuario\":\"sofia123\",\"senha\":\"SegredoInicial123\"}", "{\"nome\":\"Sofia Silva\",\"cpf\":\"55566677788\",\"usuario\":\"sofia.silva\",\"senha\":\"NovoSegredo456\"}", "sofia123", "sofia.silva"));
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
    void crudComJsonEIndices(Caso caso) throws Exception {
        String base = "/" + caso.base();
        assertThat(registros(listar(caso))).isEmpty();

        // Dois cadastros permitem conferir o deslocamento dos índices na exclusão.
        for (int i = 0; i < 2; i++) {
            mvc.perform(post(base + "/cadastrar")
                            .contentType(MediaType.APPLICATION_JSON).content(caso.cadastro()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
        }
        MvcResult resultado = listar(caso);
        assertThat(registros(resultado)).hasSize(2);
        assertThat(html(resultado)).contains(caso.original(), "<td>0</td>", "<td>1</td>");

        for (int indice : new int[] {-1, 2}) {
            mvc.perform(put(base + "/editar/" + indice)
                            .contentType(MediaType.APPLICATION_JSON).content(caso.edicao()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
            mvc.perform(delete(base + "/excluir/" + indice))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl(base));
        }
        assertThat(registros(listar(caso))).hasSize(2);
        assertThat(html(listar(caso))).contains(caso.original()).doesNotContain(caso.alterado());

        mvc.perform(delete(base + "/excluir/0"))
                .andExpect(status().isFound()).andExpect(redirectedUrl(base));
        resultado = listar(caso);
        assertThat(registros(resultado)).hasSize(1);
        assertThat(html(resultado)).contains("<td>0</td>").doesNotContain("<td>1</td>");

        mvc.perform(put(base + "/editar/0")
                        .contentType(MediaType.APPLICATION_JSON).content(caso.edicao()))
                .andExpect(status().isFound()).andExpect(redirectedUrl(base));
        resultado = listar(caso);
        assertThat(registros(resultado)).hasSize(1);
        assertThat(html(resultado)).contains(caso.alterado()).doesNotContain(caso.original());
        assertThat(html(resultado)).doesNotContain("SegredoInicial123", "NovoSegredo456", "<form", "<script");

        mvc.perform(delete(base + "/excluir/0"))
                .andExpect(status().isFound()).andExpect(redirectedUrl(base));
        assertThat(registros(listar(caso))).isEmpty();
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
