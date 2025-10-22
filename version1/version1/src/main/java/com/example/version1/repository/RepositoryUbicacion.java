package com.example.version1.repository;

import com.example.version1.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryUbicacion extends JpaRepository<Ubicacion, Long> {
}
