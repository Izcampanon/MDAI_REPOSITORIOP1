package com.example.version1.repository;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryCompra_Entrada  extends JpaRepository<Compra_Entrada,Long> {
}
