package br.com.seplag.repository;

import br.com.seplag.model.Artista;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistaRepository extends JpaRepository<Artista, Long> {
    Page<Artista> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
