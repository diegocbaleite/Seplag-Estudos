package br.com.seplag.artista;

import br.com.seplag.shared.NaoEncontradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ArtistaServiceTest {
    @Mock ArtistaRepository repository;
    @InjectMocks ArtistaService service;

    @Test
    void criaArtista() {
        when(repository.save(any(Artista.class))).thenAnswer(i -> i.getArgument(0));
        var resposta = service.criar(new ArtistaDto("Serj Tankian", TipoArtista.CANTOR));
        assertEquals("Serj Tankian", resposta.nome());
        assertEquals(TipoArtista.CANTOR, resposta.tipo());
        verify(repository).save(any(Artista.class));
    }
    @Test
    void falhaQuandoArtistaNaoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NaoEncontradoException.class, () -> service.buscar(999L));
    }
}
