package br.com.seplag.artista;

import jakarta.persistence.*;

@Entity
@Table(name = "artista")
public class Artista {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 200)
    private String nome;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoArtista tipo;
    protected Artista() {}
    public Artista(String nome, TipoArtista tipo) { this.nome = nome; this.tipo = tipo; }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public TipoArtista getTipo() { return tipo; }
    public void atualizar(String nome, TipoArtista tipo) { this.nome = nome; this.tipo = tipo; }
}
