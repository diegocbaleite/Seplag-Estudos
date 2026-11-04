package br.com.seplag.regional;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RegionalRepository extends JpaRepository<Regional, Integer> {
    List<Regional> findByAtivoTrue();
}
