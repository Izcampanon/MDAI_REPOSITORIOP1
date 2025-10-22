package com.example.version1.repository;

import com.example.version1.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryEntrada extends JpaRepository<Entrada,Long> {

}
