package br.com.seplag.service;

import br.com.seplag.dto.ArtistaDto;
import br.com.seplag.dto.ArtistaResponse;
import br.com.seplag.exception.NaoEncontradoException;
import br.com.seplag.model.Artista;
import br.com.seplag.repository.ArtistaRepository;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtistaService {
    private final ArtistaRepository repository;
    public ArtistaService(ArtistaRepository repository) { this.repository = repository; }

    @Transactional
    public ArtistaResponse criar(ArtistaDto dto) {
        return ArtistaResponse.of(repository.save(new Artista(dto.nome(), dto.tipo())));
    }

    @Transactional
    public ArtistaResponse atualizar(Long id, ArtistaDto dto) {
        Artista artista = encontrar(id);
        artista.atualizar(dto.nome(), dto.tipo());
        return ArtistaResponse.of(artista);
    }

    @Transactional(readOnly = true)
    public ArtistaResponse buscar(Long id) { return ArtistaResponse.of(encontrar(id)); }

    @Transactional(readOnly = true)
    public Page<ArtistaResponse> listar(String nome, boolean ascendente, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(ascendente ? Sort.Direction.ASC : Sort.Direction.DESC, "nome"));
        return repository.findByNomeContainingIgnoreCase(nome == null ? "" : nome, pageable).map(ArtistaResponse::of);
    }

    public Artista encontrar(Long id) {
        return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Artista não encontrado: " + id));
    }
}
