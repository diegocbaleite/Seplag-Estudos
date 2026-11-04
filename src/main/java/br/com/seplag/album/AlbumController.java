package br.com.seplag.album;

import br.com.seplag.artista.TipoArtista;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/albuns")
public class AlbumController {
    private final AlbumService service;
    public AlbumController(AlbumService service) { this.service = service; }
    @PostMapping
    public ResponseEntity<AlbumResponse> criar(@Valid @RequestBody AlbumDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }
    @PutMapping("/{id}")
    public AlbumResponse atualizar(@PathVariable Long id, @Valid @RequestBody AlbumDto dto) { return service.atualizar(id, dto); }
    @GetMapping("/{id}")
    public AlbumResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @GetMapping
    public Page<AlbumResponse> listar(@RequestParam(required = false) TipoArtista tipo,
                                      @RequestParam(defaultValue = "0") int pagina,
                                      @RequestParam(defaultValue = "10") int tamanho) {
        return service.listar(tipo, pagina, tamanho);
    }
}
