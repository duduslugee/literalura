package br.duduslugee.literalura.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LivroResponse {

    private String titulo;
    private String idioma;
    private List<AutorResponse> autores;

    // Getters e setters

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public List<AutorResponse> getAutores() {
        return autores;
    }

    public void setAutores(List<AutorResponse> autores) {
        this.autores = autores;
    }
}
