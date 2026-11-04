package br.com.seplag.capa;

import br.com.seplag.album.Album;
import jakarta.persistence.*;

@Entity
@Table(name = "capa_album")
public class CapaAlbum {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;
    @Column(name = "chave_objeto", nullable = false, unique = true)
    private String chaveObjeto;
    protected CapaAlbum() {}
    public CapaAlbum(Album album, String chaveObjeto) { this.album = album; this.chaveObjeto = chaveObjeto; }
    public Long getId() { return id; }
    public String getChaveObjeto() { return chaveObjeto; }
}
