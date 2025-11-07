package com.example.version1.repository;

import com.example.version1.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepositoryEvento extends JpaRepository<Evento, Long> {

    // Eventos por id de local
    @Query(value = "SELECT * FROM Evento e WHERE e.local_id = :localId", nativeQuery = true)
    List<Evento> findByLocalId(@Param("localId") Long localId);

    // Eventos disponibles (estado = true) por id de local
    @Query(value = "SELECT * FROM Evento e WHERE e.local_id = :localId AND e.estado = true", nativeQuery = true)
    List<Evento> findDisponiblesByLocalId(@Param("localId") Long localId);


}
