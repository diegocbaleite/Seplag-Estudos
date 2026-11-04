package br.com.seplag.repository;

import br.com.seplag.model.Album;
import br.com.seplag.model.TipoArtista;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query(value = "SELECT DISTINCT a FROM Album a LEFT JOIN a.artistas ar WHERE (:tipo IS NULL OR ar.tipo = :tipo)",
           countQuery = "SELECT COUNT(DISTINCT a.id) FROM Album a LEFT JOIN a.artistas ar WHERE (:tipo IS NULL OR ar.tipo = :tipo)")
    Page<Album> consultar(@Param("tipo") TipoArtista tipo, Pageable pageable);
}
