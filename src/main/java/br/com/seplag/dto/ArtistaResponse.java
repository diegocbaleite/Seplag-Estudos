package br.com.seplag.dto;

import br.com.seplag.model.Artista;
import br.com.seplag.model.TipoArtista;

public record ArtistaResponse(Long id, String nome, TipoArtista tipo) {
    public static ArtistaResponse of(Artista artista) {
        return new ArtistaResponse(artista.getId(), artista.getNome(), artista.getTipo());
    }
}
