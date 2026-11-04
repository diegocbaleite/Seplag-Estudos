package br.com.seplag.album;

import br.com.seplag.artista.*;
import br.com.seplag.shared.NaoEncontradoException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlbumService {
    private final AlbumRepository albums;
    private final ArtistaRepository artistas;
    private final ApplicationEventPublisher events;
    public AlbumService(AlbumRepository albums, ArtistaRepository artistas, ApplicationEventPublisher events) {
        this.albums = albums; this.artistas = artistas; this.events = events;
    }
    @Transactional
    public AlbumResponse criar(AlbumDto dto) {
        Album salvo = albums.save(new Album(dto.titulo(), resolverArtistas(dto.artistasIds())));
        AlbumResponse response = AlbumResponse.of(salvo);
        events.publishEvent(new AlbumCriadoEvent(response.id(), response.titulo()));
        return response;
    }
    @Transactional
    public AlbumResponse atualizar(Long id, AlbumDto dto) {
        Album album = encontrar(id);
        album.atualizar(dto.titulo(), resolverArtistas(dto.artistasIds()));
        return AlbumResponse.of(album);
    }
    @Transactional(readOnly = true)
    public AlbumResponse buscar(Long id) { return AlbumResponse.of(encontrar(id)); }
    @Transactional(readOnly = true)
    public Page<AlbumResponse> listar(TipoArtista tipo, int pagina, int tamanho) {
        return albums.consultar(tipo, PageRequest.of(pagina, tamanho, Sort.by("titulo").ascending())).map(AlbumResponse::of);
    }
    public Album encontrar(Long id) {
        return albums.findById(id).orElseThrow(() -> new NaoEncontradoException("Álbum não encontrado: " + id));
    }
    private Set<Artista> resolverArtistas(Set<Long> ids) {
        List<Artista> encontrados = artistas.findAllById(ids);
        if (encontrados.size() != ids.size()) throw new NaoEncontradoException("Um ou mais artistas não existem");
        return new HashSet<>(encontrados);
    }
}
