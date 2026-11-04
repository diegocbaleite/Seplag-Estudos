package br.com.seplag.album;

import br.com.seplag.shared.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlbumControllerWebTest {
    @Test
    void getAlbumRetornaJson() throws Exception {
        AlbumService servico = mock(AlbumService.class);
        when(servico.buscar(1L)).thenReturn(new AlbumResponse(1L, "Harakiri", java.util.List.of()));
        var mvc = MockMvcBuilders.standaloneSetup(new AlbumController(servico))
            .setControllerAdvice(new ApiExceptionHandler()).build();

        mvc.perform(get("/api/v1/albuns/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.titulo").value("Harakiri"));
        verify(servico).buscar(1L);
    }
}
