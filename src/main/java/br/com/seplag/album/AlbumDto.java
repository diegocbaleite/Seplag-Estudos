package br.com.seplag.album;

import jakarta.validation.constraints.*;
import java.util.Set;

public record AlbumDto(@NotBlank @Size(max = 200) String titulo, @NotEmpty Set<Long> artistasIds) {}

