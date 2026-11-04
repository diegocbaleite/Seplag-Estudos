package br.com.seplag.regional;

import jakarta.persistence.*;

@Entity
@Table(name = "regional")
public class Regional {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "codigo_externo", nullable = false)
    private Integer codigoExterno;
    @Column(nullable = false, length = 200)
    private String nome;
    @Column(nullable = false)
    private boolean ativo;
    protected Regional() {}
    public Regional(Integer codigoExterno, String nome, boolean ativo) {
        this.codigoExterno = codigoExterno; this.nome = nome; this.ativo = ativo;
    }
    public Integer getId() { return id; }
    public Integer getCodigoExterno() { return codigoExterno; }
    public String getNome() { return nome; }
    public boolean isAtivo() { return ativo; }
    public void inativar() { this.ativo = false; }
}
