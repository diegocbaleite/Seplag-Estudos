package br.com.seplag.controller;

import br.com.seplag.service.RegionalService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/regionais")
public class RegionalController {
    private final RegionalService service;
    public RegionalController(RegionalService service) { this.service = service; }
    @GetMapping
    public List<RegionalService.RegionalResponse> listar() { return service.listar(); }
    @PostMapping("/sincronizar")
    public RegionalService.ResultadoSync sincronizar() { return service.sincronizar(); }
}
