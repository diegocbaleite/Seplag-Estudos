package br.com.seplag.repository;

import br.com.seplag.model.Regional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegionalRepository extends JpaRepository<Regional, Integer> {
    List<Regional> findByAtivoTrue();
}
