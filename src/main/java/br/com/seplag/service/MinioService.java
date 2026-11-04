package br.com.seplag.service;

import br.com.seplag.exception.IntegracaoException;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {
    private final MinioClient interno;
    private final MinioClient publico;
    private final String bucket;

    public MinioService(@Value("${app.minio.url}") String url,
        @Value("${app.minio.public-url:http://localhost:9000}") String publicUrl,
        @Value("${app.minio.access-key}") String accessKey,
        @Value("${app.minio.secret-key}") String secretKey,
        @Value("${app.minio.bucket}") String bucket) {
        this.interno = MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
        this.publico = MinioClient.builder().endpoint(publicUrl).credentials(accessKey, secretKey).build();
        this.bucket = bucket;
    }
    @PostConstruct
    public void garantirBucket() {
        try {
            if (!interno.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                interno.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) { throw new IntegracaoException("Falha ao inicializar MinIO", e); }
    }
    public String enviar(MultipartFile arquivo) {
        if (arquivo.isEmpty() || arquivo.getContentType() == null ||
            !Set.of("image/jpeg","image/png","image/webp").contains(arquivo.getContentType()))
            throw new IllegalArgumentException("Envie imagens JPG, PNG ou WebP não vazias");
        String objeto = UUID.randomUUID() + "-" + Objects.requireNonNullElse(arquivo.getOriginalFilename(), "imagem");
        try (var entrada = arquivo.getInputStream()) {
            interno.putObject(PutObjectArgs.builder().bucket(bucket).object(objeto)
                .stream(entrada, arquivo.getSize(), -1).contentType(arquivo.getContentType()).build());
            return objeto;
        } catch (Exception e) { throw new IntegracaoException("Falha ao armazenar capa", e); }
    }
    public String urlAssinada(String objeto) {
        try {
            return publico.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET)
                .bucket(bucket).object(objeto).expiry(30, TimeUnit.MINUTES).build());
        } catch (Exception e) { throw new IntegracaoException("Falha ao gerar URL de capa", e); }
    }
}
