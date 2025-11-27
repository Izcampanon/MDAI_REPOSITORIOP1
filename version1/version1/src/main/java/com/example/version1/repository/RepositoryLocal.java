package com.example.version1.repository;

import com.example.version1.model.Local;
import com.example.version1.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryLocal extends JpaRepository<Local, Long> {

    // Buscar locales por nombre de ubicacion (case-insensitive, búsqueda parcial)
    @Query(value = "SELECT l.* FROM Local l JOIN Ubicacion u ON l.ubicacion_id = u.id WHERE lower(u.nombre) LIKE lower(CONCAT('%', :nombreUbicacion, '%'))", nativeQuery = true)
    List<Local> findByNombreUbicacion(@Param("nombreUbicacion") String nombreUbicacion);

    // Recuperar un Local por id (nativo)
    @Query(value = "SELECT * FROM Local l WHERE l.id = :id", nativeQuery = true)
    Optional<Local> findByIdNative(@Param("id") Long id);

    //Buscar Local por nombre y luego cargar eventos disponibles
    @Query(value = "SELECT * FROM Local l WHERE lower(l.nombre) = lower(:nombre)", nativeQuery = true)
    Optional<Local> findByNombreIgnoreCaseNative(@Param("nombre") String nombre);

    @Query(value = "SELECT * FROM Evento e WHERE e.local_id = :localId AND e.estado = true", nativeQuery = true)
    List<Evento> findEventosDisponiblesByLocalIdNative(@Param("localId") Long localId);

    @Query(value = "SELECT * FROM Evento e WHERE e.local_id = :localId", nativeQuery = true)
    List<Evento> findEventosByLocalIdNative(@Param("localId") Long localId);

    // Método público utilizado por tests y código.
    default Optional<Local> findByNombreConEventosDisponibles(@Param("nombre") String nombre) {
        Optional<Local> opt = findByNombreIgnoreCaseNative(nombre);
        opt.ifPresent(l -> {
            if (l.getId() != null) {
                List<Evento> evs = findEventosDisponiblesByLocalIdNative(l.getId());
                l.setEventos(evs);
            }
        });
        return opt;
    }

    // Proveer un equivalente a findByIdConEventos: buscar Local y asignar todos sus eventos
    default Local findByIdConEventos(@Param("id") Long id) {
        Optional<Local> opt = findByIdNative(id);
        if (opt.isPresent()) {
            Local l = opt.get();
            if (l.getId() != null) {
                List<Evento> evs = findEventosByLocalIdNative(l.getId());
                l.setEventos(evs);
            }
            return l;
        }
        return null;
    }
}
