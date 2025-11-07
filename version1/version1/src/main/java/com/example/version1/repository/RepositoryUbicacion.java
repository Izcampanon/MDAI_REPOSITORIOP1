package com.example.version1.repository;

import com.example.version1.model.Ubicacion;
import com.example.version1.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryUbicacion extends JpaRepository<Ubicacion, Long> {

    //Recuperar una Ubicacion por id y luego cargar sus locales con otra consulta
    @Query(value = "SELECT * FROM Ubicacion u WHERE u.id = :id", nativeQuery = true)
    Optional<Ubicacion> findByIdNative(@Param("id") Long id);

    @Query(value = "SELECT * FROM Local l WHERE l.ubicacion_id = :ubicacionId", nativeQuery = true)
    List<Local> findLocalesByUbicacionIdNative(@Param("ubicacionId") Long ubicacionId);

    default Ubicacion findByIdWithLocales(@Param("id") Long id) {
        Optional<Ubicacion> opt = findByIdNative(id);
        if (opt.isPresent()) {
            Ubicacion u = opt.get();
            if (u.getId() != null) {
                List<Local> locales = findLocalesByUbicacionIdNative(u.getId());
                u.setLocales(locales);
            }
            return u;
        }
        return null;
    }

    // Encontrar por nombre (ignorar mayúsculas/minúsculas)
    Optional<Ubicacion> findByNombreIgnoreCase(String nombre);

    // Comprobar existencia por nombre (ignorar mayúsculas/minúsculas)
    boolean existsByNombreIgnoreCase(String nombre);

    //Buscar la Ubicacion por nombre y cargar sus locales
    @Query(value = "SELECT * FROM Ubicacion u WHERE lower(u.nombre) = lower(:nombre)", nativeQuery = true)
    Optional<Ubicacion> findByNombreNativeIgnoreCase(@Param("nombre") String nombre);

    default Ubicacion findByNombreWithLocales(@Param("nombre") String nombre) {
        Optional<Ubicacion> opt = findByNombreNativeIgnoreCase(nombre);
        if (opt.isPresent()) {
            Ubicacion u = opt.get();
            if (u.getId() != null) {
                List<Local> locales = findLocalesByUbicacionIdNative(u.getId());
                u.setLocales(locales);
            }
            return u;
        }
        return null;
    }
}
