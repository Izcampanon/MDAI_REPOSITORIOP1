package com.example.version1.repository;

import com.example.version1.model.Compra_Entrada;
import com.example.version1.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryCompra_Entrada  extends JpaRepository<Compra_Entrada,Long> {

    @Query("SELECT e FROM Entrada e WHERE e.usuario.id = :usuarioId")
    List<Entrada> findByUsuarioId(@Param("usuarioId_Compra") Long usuarioId);
}
