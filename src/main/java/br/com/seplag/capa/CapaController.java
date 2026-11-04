package br.com.seplag.capa;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/albuns/{albumId}/capas")
public class CapaController {
    private final CapaService service;
    public CapaController(CapaService service) { this.service = service; }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CapaService.CapaResponse>> enviar(@PathVariable Long albumId,
                                               @RequestParam("arquivos") List<MultipartFile> arquivos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enviar(albumId, arquivos));
    }
    @GetMapping
    public List<CapaService.CapaResponse> listar(@PathVariable Long albumId) { return service.listar(albumId); }
}
