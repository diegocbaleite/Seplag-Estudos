package br.com.seplag.model;

import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "album")
public class Album {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String titulo;
    @ManyToMany
    @JoinTable(name = "artista_album", joinColumns = @JoinColumn(name = "album_id"), inverseJoinColumns = @JoinColumn(name = "artista_id"))
    private Set<Artista> artistas = new HashSet<>();
    protected Album() {}
    public Album(String titulo, Set<Artista> artistas) { this.titulo = titulo; this.artistas = artistas; }
    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public Set<Artista> getArtistas() { return artistas; }
    public void atualizar(String titulo, Set<Artista> artistas) { this.titulo = titulo; this.artistas = artistas; }
}
