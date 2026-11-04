package br.com.seplag.service;

import br.com.seplag.model.CapaAlbum;
import br.com.seplag.repository.CapaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Service
public class CapaService {
    private final CapaRepository capas;
    private final AlbumService albums;
    private final MinioService minio;
    public CapaService(CapaRepository capas, AlbumService albums, MinioService minio) {
        this.capas = capas; this.albums = albums; this.minio = minio;
    }
    public record CapaResponse(Long id, String url, int validadeMinutos) {}
    @Transactional
    public List<CapaResponse> enviar(Long albumId, List<MultipartFile> arquivos) {
        if (arquivos == null || arquivos.isEmpty()) throw new IllegalArgumentException("Informe uma ou mais capas");
        var album = albums.encontrar(albumId);
        List<CapaResponse> resultado = new ArrayList<>();
        for (MultipartFile arquivo : arquivos) {
            String objeto = minio.enviar(arquivo);
            var salva = capas.save(new CapaAlbum(album, objeto));
            resultado.add(new CapaResponse(salva.getId(), minio.urlAssinada(objeto), 30));
        }
        return resultado;
    }
    @Transactional(readOnly = true)
    public List<CapaResponse> listar(Long albumId) {
        albums.encontrar(albumId);
        return capas.findByAlbumId(albumId).stream()
            .map(c -> new CapaResponse(c.getId(), minio.urlAssinada(c.getChaveObjeto()), 30)).toList();
    }
}
