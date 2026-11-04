package br.com.seplag.controller;

import br.com.seplag.dto.ArtistaDto;
import br.com.seplag.dto.ArtistaResponse;
import br.com.seplag.service.ArtistaService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artistas")
public class ArtistaController {
    private final ArtistaService service;
    public ArtistaController(ArtistaService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<ArtistaResponse> criar(@Valid @RequestBody ArtistaDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }
    @PutMapping("/{id}")
    public ArtistaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ArtistaDto dto) {
        return service.atualizar(id, dto);
    }
    @GetMapping("/{id}")
    public ArtistaResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @GetMapping
    public Page<ArtistaResponse> listar(@RequestParam(defaultValue = "") String nome,
                                         @RequestParam(defaultValue = "true") boolean asc,
                                         @RequestParam(defaultValue = "0") int pagina,
                                         @RequestParam(defaultValue = "10") int tamanho) {
        return service.listar(nome, asc, pagina, tamanho);
    }
}
