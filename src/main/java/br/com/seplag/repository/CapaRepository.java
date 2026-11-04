package br.com.seplag.repository;

import br.com.seplag.model.CapaAlbum;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CapaRepository extends JpaRepository<CapaAlbum, Long> {
    List<CapaAlbum> findByAlbumId(Long albumId);
}
