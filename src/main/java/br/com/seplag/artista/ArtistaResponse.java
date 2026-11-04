package br.com.seplag.artista;

public record ArtistaResponse(Long id, String nome, TipoArtista tipo) {
    public static ArtistaResponse of(Artista artista) {
        return new ArtistaResponse(artista.getId(), artista.getNome(), artista.getTipo());
    }
}
