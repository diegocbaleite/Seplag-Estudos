package br.com.seplag.album;

import br.com.seplag.artista.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AlbumServiceTest {
    @Mock AlbumRepository albums;
    @Mock ArtistaRepository artistas;
    @Mock ApplicationEventPublisher eventos;
    @InjectMocks AlbumService service;

    @Test
    void criaAlbumRelacionandoArtistasEEmiteEvento() {
        when(artistas.findAllById(any())).thenReturn(List.of(new Artista("Michel Teló", TipoArtista.CANTOR)));
        when(albums.save(any(Album.class))).thenAnswer(i -> i.getArgument(0));
        var resposta = service.criar(new AlbumDto("Bem Sertanejo", Set.of(1L)));
        assertEquals("Bem Sertanejo", resposta.titulo());
        assertEquals(1, resposta.artistas().size());
        verify(eventos).publishEvent(any(AlbumCriadoEvent.class));
    }
    @Test
    void rejeitaArtistaInexistente() {
        when(artistas.findAllById(any())).thenReturn(List.of());
        assertThrows(br.com.seplag.shared.NaoEncontradoException.class,
            () -> service.criar(new AlbumDto("Teste", Set.of(45L))));
        verify(albums, never()).save(any());
    }
}
