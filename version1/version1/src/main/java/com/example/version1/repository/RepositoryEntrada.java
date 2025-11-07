package com.example.version1.repository;

import com.example.version1.model.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryEntrada extends JpaRepository<Entrada,Long> {

    // Buscar entradas por id de usuario (SQL nativo)
    @Query(value = "SELECT * FROM Entrada e WHERE e.usuario_id = :usuarioId", nativeQuery = true)
    List<Entrada> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    // Buscar entradas por id de evento (SQL nativo)
    @Query(value = "SELECT * FROM Entrada e WHERE e.evento_id = :eventoId", nativeQuery = true)
    List<Entrada> findByEventoId(@Param("eventoId") Long eventoId);

    // Contar entradas por evento (SQL nativo)
    @Query(value = "SELECT count(*) FROM Entrada e WHERE e.evento_id = :eventoId", nativeQuery = true)
    long countByEventoId(@Param("eventoId") Long eventoId);

    // Buscar entradas por tipo (GENERAL, VIP) (SQL nativo, case-insensitive)
    @Query(value = "SELECT * FROM Entrada e WHERE lower(e.tipo) = lower(:tipo)", nativeQuery = true)
    List<Entrada> findByTipo(@Param("tipo") String tipo);

    // Buscar entradas por nombre de usuario (columna nombre_usuario)
    @Query(value = "SELECT * FROM Entrada e WHERE lower(e.nombre_usuario) = lower(:nombreUsuario)", nativeQuery = true)
    List<Entrada> findByNombreUsuario(@Param("nombreUsuario") String nombreUsuario);

    // Buscar entrada por id (usamos el método de JpaRepository findById) - añadir helper por defecto para borrado seguro
    default boolean deleteIfEventAvailable(Long entradaId) {
        Optional<Entrada> opt = findById(entradaId);
        if (opt.isPresent()) {
            Entrada en = opt.get();
            if (en.getEvento() != null && Boolean.TRUE.equals(en.getEvento().getEstado())) {
                // Evento disponible -> permitir borrado
                deleteById(entradaId);
                return true;
            }
        }
        return false;
    }

}
