package br.com.seplag.album;

import br.com.seplag.artista.ArtistaResponse;
import java.util.List;

public record AlbumResponse(Long id, String titulo, List<ArtistaResponse> artistas) {
    public static AlbumResponse of(Album album) {
        return new AlbumResponse(album.getId(), album.getTitulo(),
            album.getArtistas().stream().map(ArtistaResponse::of).sorted((a,b) -> a.nome().compareToIgnoreCase(b.nome())).toList());
    }
}
