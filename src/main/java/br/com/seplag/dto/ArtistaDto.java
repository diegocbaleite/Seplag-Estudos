package br.com.seplag.dto;

import br.com.seplag.model.TipoArtista;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ArtistaDto(@NotBlank @Size(max = 200) String nome, @NotNull TipoArtista tipo) {}

